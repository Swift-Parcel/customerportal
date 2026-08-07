package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.Quote;
import com.swiftparcel.customerportal.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customerportal/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/quotes")
    public List<Quote> getQuoteHistory(@AuthenticationPrincipal Customer customer) {
        System.out.println("customerId from principal: " + customer.getId());
        return pricingService.getQuoteHistory(customer.getId());
    }

    @PostMapping("/quotes/{pickupRequestId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Quote createQuote(@PathVariable Long pickupRequestId) {
        return pricingService.createQuoteForPickupRequest(pickupRequestId);
    }
}