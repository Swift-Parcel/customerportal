package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ConfirmDeliveryResponse;
import com.swiftparcel.customerportal.dto.ParcelDetailResponse;
import com.swiftparcel.customerportal.dto.ScheduleResponse;
import com.swiftparcel.customerportal.service.ParcelService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.processing.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customerportal/parcel")
@RequiredArgsConstructor
public class ParcelController {
    private final ParcelService parcelService;

    @GetMapping("/{trackingNumber}")
    public ParcelDetailResponse getParcel(@PathVariable String trackingNumber) {
        return parcelService.getParcelDetails(trackingNumber);
    }

    @GetMapping("/{trackingNumber}/schedule")
    public ScheduleResponse getSchedule(@PathVariable String trackingNumber) {
        return parcelService.getSchedule(trackingNumber);
    }


    @PatchMapping("/{trackingNumber}/confirm-delivery")
    public ConfirmDeliveryResponse confirmDelivery(
            @PathVariable String trackingNumber,
            @RequestBody ConfirmDeliveryRequest request) {
        return parcelService.confirmDelivery(trackingNumber, request.customerEmail());
    }


    public record ConfirmDeliveryRequest(String customerEmail) {}

}
