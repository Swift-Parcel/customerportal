package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.PricingDTO;
import com.swiftparcel.customerportal.dto.PricingRequestDTO;
import com.swiftparcel.customerportal.model.Quote;
import com.swiftparcel.customerportal.model.Route;
import com.swiftparcel.customerportal.service.PricingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/pricing")
@AllArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping
    public PricingDTO calculatePricing(@Valid @RequestBody PricingRequestDTO pricingRequestDTO){
        return pricingService.calculateQuote(pricingRequestDTO.getServiceType(), pricingRequestDTO.getWeight(), pricingRequestDTO.getSenderAddress(), pricingRequestDTO.getRecipientAddress());
    }

    @PostMapping("/quotes")
    @ResponseStatus(HttpStatus.CREATED)
    public Quote createQuote(@Valid @RequestBody PricingRequestDTO pricingRequestDTO,
                             @RequestParam Long pickupRequestId){
        PricingDTO pricing = pricingService.calculateQuote(pricingRequestDTO.getServiceType(), pricingRequestDTO.getWeight(), pricingRequestDTO.getSenderAddress(), pricingRequestDTO.getRecipientAddress());

        Route route = pricingService.getZoneRoute(pricingRequestDTO.getSenderAddress(), pricingRequestDTO.getRecipientAddress());

        return pricingService.saveQuote(pricing, pickupRequestId, route.getRouteType());
    }
}