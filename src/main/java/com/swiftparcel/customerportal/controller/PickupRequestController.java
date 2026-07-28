package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.PickupRequestDTO;
import com.swiftparcel.customerportal.service.PickupRequestService;
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

    @PostMapping("/{customer_id}/pickup-requests")
    public ResponseEntity<String> createPickupRequest(@PathVariable Long customer_id, @RequestBody PickupRequestDTO pickupRequestDto) {
        String result = pickupRequestService.createPickupRequest(pickupRequestDto, customer_id);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }
}
