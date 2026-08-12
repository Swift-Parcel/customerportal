package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.*;
import com.swiftparcel.customerportal.service.ParcelService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customerportal/parcel")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ParcelController {
    private final ParcelService parcelService;

    @GetMapping
    public List<ParcelDTO> getParcels(
            @RequestParam String customerEmail,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer limit) {
        return parcelService.getCustomerParcels(customerEmail, skip, limit);
    }

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
