package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.*;
import com.swiftparcel.customerportal.model.Parcel;
import com.swiftparcel.customerportal.repository.ParcelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ParcelService {

    private final RestTemplate restTemplate;
    private final ParcelRepository parcelRepository;

    @Value("${app.backoffice.base-url}")
    private String backendUrl;

    @Value("${app.backoffice.api-key}")
    private String apiKey;

    public ParcelService(RestTemplate restTemplate, ParcelRepository parcelRepository) {
        this.restTemplate = restTemplate;
        this.parcelRepository = parcelRepository;
    }

    public List<ParcelDTO> getCustomerParcels(String customerEmail, Integer skip, Integer limit) {
        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels")
                .queryParam("customerEmail", customerEmail)
                .toUriString();

        ParcelResponseDTO response = restTemplate.exchange(
                url, HttpMethod.GET, buildEntity(), ParcelResponseDTO.class).getBody();

        if (response == null || response.getParcels() == null) {
            return Collections.emptyList();
        }

        return response.getParcels().stream()
                .skip(skip != null && skip > 0 ? skip : 0)
                .limit(limit != null && limit > 0 ? limit : Long.MAX_VALUE)
                .collect(Collectors.toList());
    }

    public ParcelDetailResponse getParcelDetails(String trackingNumber) {
        validate(trackingNumber);

        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}")
                .buildAndExpand(trackingNumber)
                .toUriString();

        return restTemplate.exchange(
                url, HttpMethod.GET, buildEntity(), ParcelDetailResponse.class).getBody();
    }

    public ScheduleResponse getSchedule(String trackingNumber) {
        validate(trackingNumber);

        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}/schedule")
                .buildAndExpand(trackingNumber)
                .toUriString();

        return restTemplate.exchange(
                url, HttpMethod.GET, buildEntity(), ScheduleResponse.class).getBody();
    }

    public ConfirmDeliveryResponse confirmDelivery(String trackingNumber, String customerEmail) {
        validate(trackingNumber);

        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}/confirm-delivery")
                .buildAndExpand(trackingNumber)
                .toUriString();

        Map<String, String> body = Map.of("customer_email", customerEmail);

        restTemplate.exchange(
                url, HttpMethod.PATCH, new HttpEntity<>(body, buildHeaders()), String.class);

        return new ConfirmDeliveryResponse("Delivery confirmation received");
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private HttpEntity<Void> buildEntity() {
        return new HttpEntity<>(buildHeaders());
    }

    private void validate(String trackingNumber) {
        if (trackingNumber == null || !trackingNumber.matches("^SP-[A-Z0-9]{8}$")) {
            throw new IllegalArgumentException("Invalid tracking number format: " + trackingNumber);
        }
    }

    @Transactional
    public Optional<Parcel> updateParcelStatus(ParcelStatusWebhookDTO dto) {

        Optional<Parcel> parcelOpt = parcelRepository.findByTrackingNumber(dto.getTrackingNumber());

        if (parcelOpt.isEmpty()) {
            log.warn("No local parcel found for tracking number {}. Skipping notification.",
                    dto.getTrackingNumber());
            return Optional.empty();
        }

        Parcel parcel = parcelOpt.get();

        if (parcel.getStatus() == dto.getParcelStatus()) {
            log.info("Parcel {} already has status {}. Skipping.",
                    parcel.getTrackingNumber(), parcel.getStatus());
            return Optional.empty();
        }

        log.info("Updating parcel {} status: {} -> {}",
                parcel.getTrackingNumber(), parcel.getStatus(), dto.getParcelStatus());

        parcel.setStatus(dto.getParcelStatus());
        return Optional.of(parcelRepository.save(parcel));
    }
}