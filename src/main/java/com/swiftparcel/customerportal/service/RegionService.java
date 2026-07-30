package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.RegionDTO;
import com.swiftparcel.customerportal.model.Region;
import com.swiftparcel.customerportal.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    public List<RegionDTO> getAllRegions(){
        return regionRepository.findAll().stream()
                .map(r -> new RegionDTO(
                        r.getId(),
                        r.getCode(),
                        r.getCity(),
                        r.getCountryCode(),
                        r.getTimezone()
                ))
                .toList();
    }
}
