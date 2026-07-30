package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.RegionDTO;
import com.swiftparcel.customerportal.model.Region;
import com.swiftparcel.customerportal.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customerportal")
public class RegionController {
    private final RegionService regionService;

    public RegionController(RegionService regionService){
        this.regionService = regionService;
    }

    @GetMapping("/regions")
    public List<RegionDTO> getAvailableRegions() {
        return regionService.getAllRegions();
    }
}
