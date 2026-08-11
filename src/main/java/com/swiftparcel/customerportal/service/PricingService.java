package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.ConfirmQuoteResponse;
import com.swiftparcel.customerportal.dto.PricingDTO;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.model.Parcel;
import com.swiftparcel.customerportal.model.PickupRequest;
import com.swiftparcel.customerportal.model.Quote;
import com.swiftparcel.customerportal.model.Route;
import com.swiftparcel.customerportal.model.ServiceRate;
import com.swiftparcel.customerportal.model.enums.CurrentStatus;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import com.swiftparcel.customerportal.repository.AddressRepository;
import com.swiftparcel.customerportal.repository.ParcelRepository;
import com.swiftparcel.customerportal.repository.PickupRequestRepository;
import com.swiftparcel.customerportal.repository.QuotesRepository;
import com.swiftparcel.customerportal.repository.RouteRepository;
import com.swiftparcel.customerportal.repository.ServiceRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

    private static final Set<CurrentStatus> SUBMITTABLE = EnumSet.of(
            CurrentStatus.QUOTED,
            CurrentStatus.CONFIRMED,
            CurrentStatus.SUBMITTED_TO_BACKOFFICE);

    private final RouteRepository routeRepository;
    private final ServiceRateRepository serviceRateRepository;
    private final QuotesRepository quotesRepository;
    private final PickupRequestRepository pickupRequestRepository;
    private final AddressRepository addressRepository;
    private final ParcelRepository parcelRepository;
    private final BackOfficeParcelService backofficeParcelService;

    public List<Quote> getQuoteHistory(Long customerId) {
        Instant since = Instant.now().minus(30, ChronoUnit.DAYS);
        return quotesRepository.findQuoteHistory(customerId, since);
    }

    @Transactional
    public Quote createQuoteForPickupRequest(Long pickupRequestId, Long customerId) {
        PickupRequest pickupRequest = pickupRequestRepository.findById(pickupRequestId)
                .orElseThrow(() -> new IllegalStateException("Pickup request not found: " + pickupRequestId));

        if (!pickupRequest.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("Pickup request does not belong to the customer");
        }

        Address senderAddress = loadAddress(pickupRequest.getSenderAddress());
        Address recipientAddress = loadAddress(pickupRequest.getRecipientAddress());

        BigDecimal weight = new BigDecimal(Float.toString(pickupRequest.getParcelWeight()));

        PricingDTO pricing = calculateQuote(
                pickupRequest.getServiceType(), weight, senderAddress, recipientAddress);

        Route route = getZoneRoute(senderAddress, recipientAddress);

        Quote quote = saveQuote(pricing, pickupRequestId, route.getRouteType());

        pickupRequest.setCurrentStatus(CurrentStatus.QUOTED);
        pickupRequestRepository.save(pickupRequest);

        return quote;
    }

    public PricingDTO calculateQuote(ServiceType serviceType, BigDecimal weight,
                                     Address senderAddress, Address recipientAddress) {

        ServiceRate rate = serviceRateRepository.findByServiceType(serviceType)
                .orElseThrow(() -> new IllegalStateException("Missing rate configuration for: " + serviceType));

        Route route = getZoneRoute(senderAddress, recipientAddress);

        BigDecimal basePrice     = rate.getBasePrice();
        BigDecimal weightCharge  = rate.getPerKgRate().multiply(weight);
        BigDecimal surcharge     = BigDecimal.ZERO;

        BigDecimal threshold = rate.getSurchargeWeightThresholdKg();
        if (threshold != null && weight.compareTo(threshold) > 0) {
            surcharge = rate.getSurchargeAmount();
        }

        BigDecimal subtotal = basePrice.add(weightCharge).add(surcharge);

        BigDecimal totalPrice = subtotal.multiply(route.getMultiplier())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal displayBase      = basePrice.setScale(2, RoundingMode.HALF_UP);
        BigDecimal displayWeight    = weightCharge.setScale(2, RoundingMode.HALF_UP);
        BigDecimal displaySurcharge = surcharge.setScale(2, RoundingMode.HALF_UP);

        BigDecimal zoneAdjustment = totalPrice
                .subtract(displayBase)
                .subtract(displayWeight)
                .subtract(displaySurcharge);

        return PricingDTO.builder()
                .basePrice(displayBase)
                .weightCharge(displayWeight)
                .surcharge(displaySurcharge)
                .zoneAdjustment(zoneAdjustment)
                .totalPrice(totalPrice)
                .build();
    }

    public Route getZoneRoute(Address address1, Address address2) {
        List<Route> routes = routeRepository.findAll();

        if (!Objects.equals(address1.getCountryCode(), address2.getCountryCode())) {
            return getRouteOrThrow(routes, "CROSS_COUNTRY");
        }

        if (address1.getCity().equals(address2.getCity())) {
            return getRouteOrThrow(routes, "SAME_CITY");
        } else {
            return getRouteOrThrow(routes, "SAME_COUNTRY");
        }
    }

    private Route getRouteOrThrow(List<Route> routes, String routeType) {
        return routes.stream()
                .filter(r -> r.getRouteType().equals(routeType))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Missing route configuration for: " + routeType));
    }

    @Transactional
    public Quote saveQuote(PricingDTO pricingDTO, Long pickupRequestId, String routeType) {
        Instant now = Instant.now();

        Quote quote = Quote.builder()
                .pickupRequestId(pickupRequestId)
                .basePrice(pricingDTO.getBasePrice())
                .weightCharge(pricingDTO.getWeightCharge())
                .surcharge(pricingDTO.getSurcharge())
                .zoneAdjustment(pricingDTO.getZoneAdjustment())
                .totalPrice(pricingDTO.getTotalPrice())
                .quoteRouteType(routeType)
                .quotedAt(now)
                .quoteExpiresAt(now.plus(24, ChronoUnit.HOURS))
                .build();

        return quotesRepository.save(quote);
    }


    @Transactional
    public ConfirmQuoteResponse confirmQuote(Long quoteId, Long customerId) {

        Quote quote = quotesRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalStateException("Quote not found: " + quoteId));

        PickupRequest pickupRequest = pickupRequestRepository.findByIdForUpdate(quote.getPickupRequestId())
                .orElseThrow(() -> new IllegalStateException(
                        "Pickup request not found: " + quote.getPickupRequestId()));

        if (!pickupRequest.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("Pickup request does not belong to the customer");
        }

        if (pickupRequest.getTrackingNumber() != null) {
            return buildConfirmResponse(pickupRequest);
        }

        if (!SUBMITTABLE.contains(pickupRequest.getCurrentStatus())) {
            throw new IllegalStateException(
                    "Pickup request " + pickupRequest.getId() + " cannot be confirmed from status "
                            + pickupRequest.getCurrentStatus());
        }

        if (pickupRequest.getCurrentStatus() == CurrentStatus.QUOTED) {
            if (quote.getQuoteExpiresAt().isBefore(Instant.now())) {
                pickupRequest.setCurrentStatus(CurrentStatus.EXPIRED);
                throw new IllegalStateException(
                        "Quote " + quoteId + " expired at " + quote.getQuoteExpiresAt());
            }

            pickupRequest.setAcceptedQuoteId(quote.getId());
            pickupRequest.setPaidAt(Instant.now());
            pickupRequest.setCurrentStatus(CurrentStatus.CONFIRMED);
            log.info("Pickup request {} confirmed at {} EUR from quote {}",
                    pickupRequest.getId(), quote.getTotalPrice(), quoteId);
        }


        pickupRequest.setCurrentStatus(CurrentStatus.SUBMITTED_TO_BACKOFFICE);

        ConfirmQuoteResponse.BackofficeResponse response =
                backofficeParcelService.createParcel(pickupRequest);

        pickupRequest.setTrackingNumber(response.getTrackingNumber());
        pickupRequest.setCurrentStatus(CurrentStatus.TRACKING_NUMBER_ASSIGNED);


        Parcel parcel = parcelRepository.findByPickupRequestId(pickupRequest.getId())
                .orElseGet(Parcel::new);

        parcel.setPickupRequestId(pickupRequest.getId());
        parcel.setCustomerId(pickupRequest.getCustomerId());
        parcel.setTrackingNumber(response.getTrackingNumber());
        parcel.setStatus(backofficeParcelService.toParcelStatus(response.getParcelStatus()));
        parcelRepository.save(parcel);
        log.info("Pickup request {} assigned tracking number {}",
                pickupRequest.getId(), response.getTrackingNumber());

        return buildConfirmResponse(pickupRequest);
    }

    private Address loadAddress(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalStateException("Address not found: " + addressId));
    }

    private ConfirmQuoteResponse buildConfirmResponse(PickupRequest pickupRequest) {
        BigDecimal totalPrice = pickupRequest.getAcceptedQuoteId() == null
                ? null
                : quotesRepository.findById(pickupRequest.getAcceptedQuoteId())
                .map(Quote::getTotalPrice)
                .orElse(null);

        return ConfirmQuoteResponse.builder()
                .pickupRequestId(pickupRequest.getId())
                .quoteId(pickupRequest.getAcceptedQuoteId())
                .totalPriceEur(totalPrice)
                .trackingNumber(pickupRequest.getTrackingNumber())
                .status(pickupRequest.getCurrentStatus().name())
                .paidAt(pickupRequest.getPaidAt())
                .build();
    }
}