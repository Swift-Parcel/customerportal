package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;
import com.swiftparcel.customerportal.dto.PickupRequestDTO;
import com.swiftparcel.customerportal.service.PickupRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/customer")
@SecurityRequirement(name = "bearerAuth")
public class PickupRequestController {

    private final PickupRequestService pickupRequestService;

    public PickupRequestController(PickupRequestService pickupRequestService){
        this.pickupRequestService = pickupRequestService;
    }

    @PostMapping("/{customerId}/pickup-requests")
    public ResponseEntity<ApiResponse> createPickupRequest(@PathVariable Long customerId, @Valid @RequestBody PickupRequestDTO pickupRequestDto) {
        String result = pickupRequestService.createPickupRequest(pickupRequestDto, customerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(result));
    }
}
