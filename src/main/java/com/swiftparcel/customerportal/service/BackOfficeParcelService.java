package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.BackOfficeParcelDTO;
import com.swiftparcel.customerportal.dto.ConfirmQuoteResponse;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.model.PickupRequest;
import com.swiftparcel.customerportal.model.enums.ParcelStatus;
import com.swiftparcel.customerportal.model.enums.TimeSlot;
import com.swiftparcel.customerportal.repository.AddressRepository;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class BackOfficeParcelService {

    @Value("${app.backoffice.base-url}")
    private String backendUrl;

    private static final ZoneId DEFAULT_PICKUP_ZONE = ZoneId.of("Europe/Budapest");

    private static final DateTimeFormatter UTC_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final RestTemplate restTemplate;

    public ConfirmQuoteResponse.BackofficeResponse createParcel(PickupRequest pickupRequest) {

        BackOfficeParcelDTO request = buildRequest(pickupRequest);

        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels")
                .toUriString();

        ConfirmQuoteResponse.BackofficeResponse response;
        try {
            response = restTemplate.postForObject(
                    url, request, ConfirmQuoteResponse.BackofficeResponse.class);
        } catch (RestClientException e) {
            log.error("Back-office unreachable submitting pickup request {}", pickupRequest.getId(), e);
            throw new IllegalStateException(
                    "Pickup request " + pickupRequest.getId()
                            + " could not be submitted to the back-office", e);
        }

        if (response == null || response.getTrackingNumber() == null) {
            throw new IllegalStateException(
                    "Back-office returned no tracking number for pickup request " + pickupRequest.getId());
        }

        return response;
    }

    public ParcelStatus toParcelStatus(String value) {
        if (value == null) {
            return ParcelStatus.PENDING_PICKUP;
        }
        try {
            return ParcelStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown parcel status from back-office: {}. Defaulting to PENDING_PICKUP.", value);
            return ParcelStatus.PENDING_PICKUP;
        }
    }

    private BackOfficeParcelDTO buildRequest(PickupRequest pickupRequest) {

        String senderEmail = customerRepository.findById(pickupRequest.getCustomerId())
                .orElseThrow(() -> new IllegalStateException(
                        "Customer not found: " + pickupRequest.getCustomerId()))
                .getEmail();

        Address senderAddress = loadAddress(pickupRequest.getSenderAddress());
        Address recipientAddress = loadAddress(pickupRequest.getRecipientAddress());

        return BackOfficeParcelDTO.builder()
                .sender(BackOfficeParcelDTO.Sender.builder()
                        .email(senderEmail)
                        .senderAddress(toBackofficeAddress(senderAddress))
                        .build())
                .recipient(BackOfficeParcelDTO.Recipient.builder()
                        .name(pickupRequest.getRecipientName())
                        .recipientAddress(toBackofficeAddress(recipientAddress))
                        .build())
                .parcel(BackOfficeParcelDTO.Parcel.builder()
                        .weight(new BigDecimal(Float.toString(pickupRequest.getParcelWeight())))
                        .height(pickupRequest.getParcelHeight())
                        .width(pickupRequest.getParcelWidth())
                        .length(pickupRequest.getParcelLength())
                        .serviceType(pickupRequest.getServiceType().name())
                        .declaredValue(pickupRequest.getDeclaredValue())
                        .preferredPickupDate(toUtcString(
                                pickupRequest.getPreferredPickupDate(),
                                pickupRequest.getPreferredTimeSlot(),
                                senderAddress.getCountryCode()))
                        .preferredPickupTimeslot(pickupRequest.getPreferredTimeSlot().name())
                        .build())
                .build();
    }

    private Address loadAddress(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalStateException("Address not found: " + addressId));
    }

    private BackOfficeParcelDTO.BackofficeAddress toBackofficeAddress(Address address) {
        return BackOfficeParcelDTO.BackofficeAddress.builder()
                .city(address.getCity())
                .countryCode(address.getCountryCode())
                .postalCode(address.getPostalCode())
                .street(address.getStreet())
                .streetNumber(address.getStreetNumber())
                .build();
    }

    private String toUtcString(LocalDate date, TimeSlot slot, String countryCode) {
        LocalTime start = switch (slot) {
            case MORNING -> LocalTime.of(8, 0);
            case AFTERNOON -> LocalTime.of(12, 0);
            case EVENING -> LocalTime.of(17, 0);
        };
        return UTC_FORMAT.format(date.atTime(start).atZone(DEFAULT_PICKUP_ZONE));
    }


}