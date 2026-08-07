package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.DeliveryChangeResponseDTO;
import com.swiftparcel.customerportal.dto.DeliveryChangeRequestDTO;
import com.swiftparcel.customerportal.dto.DeliveryChangeDTO;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.DeliveryChangeRequest;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeRequestOutcome;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeStatus;
import com.swiftparcel.customerportal.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final RestTemplate restTemplate;

    @Value("${app.backoffice.base-url}")
    private String backendUrl;

    @Value("${app.backoffice.api-key}")
    private String apiKey;

    @Transactional
    public DeliveryChangeRequest createRequest(Customer customer, DeliveryChangeRequestDTO dto) {
        validateNoPendingRequest(dto.getTrackingNumber());

        DeliveryChangeRequest request = DeliveryChangeRequest.builder()
                .customer(customer)
                .trackingNumber(dto.getTrackingNumber())
                .requestedDate(dto.getRequestedDate())
                .requestedSlot(dto.getRequestedSlot() != null ? dto.getRequestedSlot().name() : null)
                .status(DeliveryChangeStatus.REQUESTED)
                .build();

        request = deliveryRepository.save(request);

        String caseNumber = callBackendForDeliveryChange(dto);

        if (caseNumber != null) {
            request.setCaseNumber(caseNumber);
            request.setStatus(DeliveryChangeStatus.PENDING_REVIEW);
            request = deliveryRepository.save(request);
        }

        return request;
    }

    private void validateNoPendingRequest(String trackingNumber) {
        List<DeliveryChangeStatus> pendingStatuses = List.of(DeliveryChangeStatus.REQUESTED, DeliveryChangeStatus.PENDING_REVIEW);
        boolean hasPending = deliveryRepository.findByTrackingNumber(trackingNumber).stream()
                .anyMatch(r -> pendingStatuses.contains(r.getStatus()));

        if (hasPending) {
            throw new IllegalArgumentException("A pending delivery change request already exists for this parcel.");
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

            if (request.getStatus() == DeliveryChangeStatus.APPROVED || request.getStatus() == DeliveryChangeStatus.REJECTED) {
                log.info("Request for case {} already processed with status {}. Skipping.",
                        request.getCaseNumber(), request.getStatus());
                return Optional.empty();
            }

            if (request.getStatus() == DeliveryChangeStatus.PENDING_REVIEW) {
                if (deliveryChangeDTO.getOutcome() == DeliveryChangeRequestOutcome.APPROVED) {
                    request.setStatus(DeliveryChangeStatus.APPROVED);
                } else if (deliveryChangeDTO.getOutcome() == DeliveryChangeRequestOutcome.REJECTED) {
                    request.setStatus(DeliveryChangeStatus.REJECTED);
                }
                return Optional.of(deliveryRepository.save(request));
            }
        }
        return Optional.empty();
    }
}
