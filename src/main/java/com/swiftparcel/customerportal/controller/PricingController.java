package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.PricingDTO;
import com.swiftparcel.customerportal.dto.PricingRequestDTO;
import com.swiftparcel.customerportal.service.PricingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

}

