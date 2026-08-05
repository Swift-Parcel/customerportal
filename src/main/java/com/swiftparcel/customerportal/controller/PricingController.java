package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.model.Quote;
import com.swiftparcel.customerportal.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customerportal/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/quotes/{pickupRequestId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Quote createQuote(@PathVariable Long pickupRequestId) {
        return pricingService.createQuoteForPickupRequest(pickupRequestId);
    }
}