package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;
import com.swiftparcel.customerportal.dto.DeliveryChangeRequestDTO;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.service.DeliveryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/customer/{customerId}/delivery-change")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<ApiResponse> createDeliveryChangeRequest(
            @PathVariable Long customerId,
            @Valid @RequestBody DeliveryChangeRequestDTO requestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer principal = (Customer) authentication.getPrincipal();

        if (!principal.getId().equals(customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("You can only request delivery changes for your own account"));
        }


        deliveryService.createRequest(principal, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Delivery change request submitted successfully"));
    }
}
