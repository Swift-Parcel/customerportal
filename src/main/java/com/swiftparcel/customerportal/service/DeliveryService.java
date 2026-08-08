package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.*;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.DeliveryChangeRequest;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeRequestOutcome;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeStatus;
import com.swiftparcel.customerportal.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
// http://localhost:3500/api/integration/parcels/SP-20261016
// http://localhost:3500/api/integration/parcels/SP-20261016
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final RestTemplate restTemplate;
    private final ParcelService parcelService;

    @Value("${app.backoffice.base-url}")
    private String backendUrl;

    @Value("${app.backoffice.api-key}")
    private String apiKey;

    public DeliveryChangeRequest createRequest(Customer customer, DeliveryChangeRequestDTO dto) {
        validateNoPendingRequest(dto.getTrackingNumber());
        validateInTransit(dto.getTrackingNumber());

        String caseNumber = callBackendForDeliveryChange(dto);

        if (caseNumber == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create delivery change case in backend.");
        }

        DeliveryChangeRequest request = DeliveryChangeRequest.builder()
                .customer(customer)
                .trackingNumber(dto.getTrackingNumber())
                .caseNumber(caseNumber)
                .requestedDate(dto.getRequestedDate())
                .requestedSlot(dto.getRequestedSlot() != null ? dto.getRequestedSlot().name() : null)
                .status(DeliveryChangeStatus.PENDING_REVIEW)
                .build();

        return deliveryRepository.save(request);
    }

    private void validateInTransit(String trackingNumber) {
        String status = fetchParcelStatus(trackingNumber);
        if (!"IN_TRANSIT".equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery change requests can only be made for parcels with status IN_TRANSIT.");
        }
    }

    private String fetchParcelStatus(String trackingNumber) {
        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}")
                .buildAndExpand(trackingNumber)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ParcelStatusDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ParcelStatusDTO.class);
            if (response.getBody() != null) {
                return response.getBody().getParcelStatus();
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Received empty response from back-office.");
        } catch (RestClientException e) {
            log.error("Error fetching parcel status: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Back-office is unreachable: " + e.getMessage());
        }
    }

    private void validateNoPendingRequest(String trackingNumber) {
        List<DeliveryChangeStatus> pendingStatuses = List.of(DeliveryChangeStatus.REQUESTED, DeliveryChangeStatus.PENDING_REVIEW);
        boolean hasPending = deliveryRepository.existsByTrackingNumberAndStatusIn(trackingNumber, pendingStatuses);

        if (hasPending) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A pending delivery change request already exists for this parcel.");
        }
    }

    private String callBackendForDeliveryChange(DeliveryChangeRequestDTO dto) {
        String createUrl = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}/delivery-change")
                .buildAndExpand(dto.getTrackingNumber())
                .toUriString();

        String dateStr = null;
        if (dto.getRequestedDate() != null) {
            ZonedDateTime zdt = dto.getRequestedDate().atStartOfDay(ZoneId.of("UTC"));
            dateStr = zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        String timeslotStr = dto.getRequestedSlot() != null ? dto.getRequestedSlot().name() : null;

        Map<String, Object> caseRequestBody = new java.util.HashMap<>();
        caseRequestBody.put("date", dateStr);
        caseRequestBody.put("timeslot", timeslotStr);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(caseRequestBody, headers);

        try {
            DeliveryChangeResponseDTO response = restTemplate.postForObject(createUrl, entity, DeliveryChangeResponseDTO.class);
            return response != null ? response.getCaseNumber() : null;
        } catch (RestClientException e) {
            log.error("Error calling backend for delivery change: {}", e.getMessage());
        }
        return null;
    }

    @Transactional
    public Optional<DeliveryChangeRequest> updateDeliveryChangeRequest(DeliveryChangeDTO deliveryChangeDTO) {
        Optional<DeliveryChangeRequest> requestOpt = deliveryRepository.findByCaseNumber(deliveryChangeDTO.getCaseNumber());

        if (requestOpt.isPresent()) {
            DeliveryChangeRequest request = requestOpt.get();

            if (request.getStatus() != DeliveryChangeStatus.PENDING_REVIEW) {
                return Optional.empty();
            }

            if (deliveryChangeDTO.getOutcome() == DeliveryChangeRequestOutcome.APPROVED) {
                request.setStatus(DeliveryChangeStatus.APPROVED);
            } else if (deliveryChangeDTO.getOutcome() == DeliveryChangeRequestOutcome.REJECTED) {
                request.setStatus(DeliveryChangeStatus.REJECTED);
            }

            DeliveryChangeRequest updatedRequest = deliveryRepository.save(request);
            return Optional.of(updatedRequest);
        }
        return Optional.empty();
    }
}
