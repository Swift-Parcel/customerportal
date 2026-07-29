package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.ParcelDTO;
import com.swiftparcel.customerportal.dto.ParcelResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParcelService {
    private final RestTemplate restTemplate;

    @Value("${external.api.backoffice-url}")
    private String backOfficeUrl;

    public ParcelService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ParcelDTO> getCustomerParcels(String customerEmail, Integer skip, Integer limit) {
        String url = UriComponentsBuilder.fromUriString(backOfficeUrl)
                .queryParam("customerEmail", customerEmail)
                .toUriString();

        ParcelResponseDTO response = restTemplate.getForObject(url, ParcelResponseDTO.class);

        if (response == null || response.getParcels() == null) {
            return Collections.emptyList();
        }

        List<ParcelDTO> parcels = response.getParcels().stream()
                .skip(skip != null && skip > 0 ? skip : 0)
                .limit(limit != null && limit > 0 ? limit : Long.MAX_VALUE)
                .collect(Collectors.toList());

        return parcels;
    }
}
