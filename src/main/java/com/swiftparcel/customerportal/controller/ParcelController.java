package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ParcelDetailResponse;
import com.swiftparcel.customerportal.dto.ScheduleResponse;
import com.swiftparcel.customerportal.service.ParcelService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.processing.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customerportal/parcel")
@RequiredArgsConstructor
public class ParcelController{
    private final ParcelService parcelService;

    @GetMapping("/{trackingNumber}")
    public ParcelDetailResponse getParcel(@PathVariable String trackingNumber) {
        return parcelService.getParcelDetails(trackingNumber);
    }

    @GetMapping("/{trackingNumber}/schedule")
    public ScheduleResponse getSchedule(@PathVariable String trackingNumber) {
        return parcelService.getSchedule(trackingNumber);
    }
}
