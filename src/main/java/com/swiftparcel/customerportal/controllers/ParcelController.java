package com.swiftparcel.customerportal.controllers;

import com.swiftparcel.customerportal.dto.ParcelDTO;
import com.swiftparcel.customerportal.services.ParcelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customerportal/parcel")
public class ParcelController {
    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping
    public List<ParcelDTO> getParcels(
            @RequestParam String customerEmail,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer limit) {
        return parcelService.getCustomerParcels(customerEmail, skip, limit);
    }
}
