package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.PricingDTO;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.model.Route;
import com.swiftparcel.customerportal.model.ServiceRate;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import com.swiftparcel.customerportal.repository.RegionRepository;
import com.swiftparcel.customerportal.repository.RouteRepository;
import com.swiftparcel.customerportal.repository.ServiceRateRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final RouteRepository routeRepository;
    private final ServiceRateRepository serviceRateRepository;
    private final RegionRepository regionRepository;


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

    public Route getZoneRoute(Address address1, Address address2){
        List<Route> routes = routeRepository.findAll();

        if(!Objects.equals(address1.getCountryCode(), address2.getCountryCode())){
            return routes.stream()
                    .filter(r -> r.getRouteType().equals("CROSS_COUNTRY"))
                    .findAny()
                    .orElse(null);
        }

        if(address1.getCity().equals(address2.getCity())){
            return routes.stream()
                    .filter(r -> r.getRouteType().equals("SAME_CITY"))
                    .findAny()
                    .orElse(null);

        }else{
            return routes.stream()
                    .filter(r -> r.getRouteType().equals("SAME_COUNTRY"))
                    .findAny()
                    .orElse(null);
        }
    }

}
