package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.PickupRequestDTO;
import com.swiftparcel.customerportal.service.PickupRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/customer")
public class PickupRequestController {

    private final PickupRequestService pickupRequestService;

    public PickupRequestController(PickupRequestService pickupRequestService){
        this.pickupRequestService = pickupRequestService;
    }

    @PostMapping("/{customerId}/pickup-requests")
    public ResponseEntity<String> createPickupRequest(@PathVariable Long customerId, @Valid @RequestBody PickupRequestDTO pickupRequestDto) {
        String result = pickupRequestService.createPickupRequest(pickupRequestDto, customerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }
}
