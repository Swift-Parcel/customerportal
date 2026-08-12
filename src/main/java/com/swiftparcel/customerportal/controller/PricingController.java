package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ConfirmQuoteResponse;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.PickupRequest;
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
    public Quote createQuote(@PathVariable Long pickupRequestId,
                             @AuthenticationPrincipal Customer customer) {
        return pricingService.createQuoteForPickupRequest(pickupRequestId, customer.getId());
    }

    @PostMapping("/quotes/confirm-quote/{quoteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ConfirmQuoteResponse confirmQuote(@PathVariable Long quoteId,
                                             @AuthenticationPrincipal Customer customer) {
        System.out.println("customerId from principal: " + customer.getId());
        return pricingService.confirmQuote(quoteId, customer.getId());
    }
}