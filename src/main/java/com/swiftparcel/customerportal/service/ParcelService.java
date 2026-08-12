package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.*;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.Parcel;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import com.swiftparcel.customerportal.repository.ParcelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
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
    private final CustomerRepository customerRepository;

    @Value("${app.backoffice.base-url}")
    private String backendUrl;

    @Value("${app.backoffice.api-key}")
    private String apiKey;

    public ParcelService(RestTemplate restTemplate, ParcelRepository parcelRepository, CustomerRepository customerRepository) {
        this.restTemplate = restTemplate;
        this.parcelRepository = parcelRepository;
        this.customerRepository = customerRepository;
    }

    public List<ParcelDTO> getCustomerParcels(String customerEmail, Integer skip, Integer limit) {
        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels")
                .queryParam("customerEmail", customerEmail)
                .toUriString();

        ParcelDTO[] response = restTemplate.exchange(
                url, HttpMethod.GET, buildEntity(), ParcelDTO[].class).getBody();

        if (response == null) {
            return Collections.emptyList();
        }

        return java.util.Arrays.stream(response)
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

        try {
            return restTemplate.exchange(
                    url, HttpMethod.GET, buildEntity(), ParcelDetailResponse.class).getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Parcel {} not found via direct tracking endpoint. Attempting fallback.", trackingNumber);
            return attemptFallback(trackingNumber);
        }
    }

    private ParcelDetailResponse attemptFallback(String trackingNumber) {

        Optional<Parcel> parcelOpt = parcelRepository.findByTrackingNumber(trackingNumber);
        if (parcelOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no tracking information available for parcel with tracking number " + trackingNumber);
        }

        Long customerId = parcelOpt.get().getCustomerId();
        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no tracking information available for parcel with tracking number " + trackingNumber);
        }

        String customerEmail = customerRepository.findById(customerId)
                .map(Customer::getEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no tracking information available for parcel with tracking number " + trackingNumber));

        List<ParcelDTO> parcels = getCustomerParcels(customerEmail, 0, Integer.MAX_VALUE);

        return parcels.stream()
                .filter(p -> trackingNumber.equals(p.getTrackingNumber()))
                .findFirst()
                .map(p -> {
                    ParcelDetailResponse.Location currentLocation = null;
                    if (p.getRecipient() != null && p.getRecipient().getAddress() != null) {
                        AddressDTO addr = p.getRecipient().getAddress();
                        currentLocation = ParcelDetailResponse.Location.builder()
                                .city(addr.getCity())
                                .countryCode(addr.getCountryCode())
                                .postalCode(addr.getPostalCode())
                                .build();
                    }

                    return ParcelDetailResponse.builder()
                            .parcelStatus(p.getStatus())
                            .location(currentLocation)
                            .trackingHistory(Collections.singletonList(
                                    ParcelDetailResponse.TrackingEvent.builder()
                                            .parcelStatus(p.getStatus())
                                            .location(currentLocation)
                                            .build()
                            ))
                            .build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no tracking information available for parcel with tracking number " + trackingNumber));
    }

    public ScheduleResponse getSchedule(String trackingNumber) {
        validate(trackingNumber);

        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}/delivery-estimate")
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