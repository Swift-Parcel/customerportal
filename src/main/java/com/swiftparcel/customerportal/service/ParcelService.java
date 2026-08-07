package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.ConfirmDeliveryResponse;
import com.swiftparcel.customerportal.dto.ParcelDetailResponse;
import com.swiftparcel.customerportal.dto.ScheduleResponse;
import com.swiftparcel.customerportal.dto.ParcelDTO;
import com.swiftparcel.customerportal.dto.ParcelResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;


@Service
public class ParcelService {


    private final RestTemplate restTemplate;

    @Value("${app.backoffice.base-url}")
    private String backendUrl;

    public ParcelService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ParcelDTO> getCustomerParcels(String customerEmail, Integer skip, Integer limit) {
        String url = UriComponentsBuilder.fromUriString(backendUrl)
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

    public ParcelDetailResponse getParcelDetails(String trackingNumber) {
        validate(trackingNumber);

        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .queryParam("trackingNumber", trackingNumber)
                .toUriString();


        return restTemplate.getForObject(
                url,
                ParcelDetailResponse.class);
    }

    public ScheduleResponse getSchedule(String trackingNumber) {
        validate(trackingNumber);

        String url2 = UriComponentsBuilder.fromUriString(backendUrl)
                .queryParam("trackingNumber", trackingNumber)
                .toUriString();


        return restTemplate.getForObject(
                url2,
                ScheduleResponse.class);
    }

    private void validate(String trackingNumber) {
        if (trackingNumber == null || !trackingNumber.matches("^SP-[A-Z0-9]{8}$")) {
            throw new IllegalArgumentException("Invalid tracking number format: " + trackingNumber);
        }
    }

    public ConfirmDeliveryResponse confirmDelivery(String trackingNumber, String customerEmail) {
        validate(trackingNumber);

        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}/confirm-delivery")
                .buildAndExpand(trackingNumber)
                .toUriString();

        Map<String, String> body = Map.of("customer_email", customerEmail);

        restTemplate.patchForObject(url, body, String.class);

        return new ConfirmDeliveryResponse("Delivery confirmation received");
    }

}
