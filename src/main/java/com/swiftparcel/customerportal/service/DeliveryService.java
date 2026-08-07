package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.DeliveryChangeRequestDTO;
import com.swiftparcel.customerportal.dto.DeliveryChangeDTO;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.DeliveryChangeRequest;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeRequestOutcome;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeStatus;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import com.swiftparcel.customerportal.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

    @Value("${external.api.backoffice-url}")
    private String backendUrl;

    @Value("${app.key.api-key}")
    private String apiKey;

    @Transactional
    public DeliveryChangeRequest createRequest(Customer customer, DeliveryChangeRequestDTO dto) {
        String url = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}")
                .buildAndExpand(dto.getTrackingNumber())
                .toUriString();


        List<DeliveryChangeStatus> pendingStatuses = List.of(DeliveryChangeStatus.REQUESTED, DeliveryChangeStatus.PENDING_REVIEW);
        List<DeliveryChangeRequest> existingPendingRequests = deliveryRepository
                .findByTrackingNumber(dto.getTrackingNumber()).stream()
                .filter(r -> pendingStatuses.contains(r.getStatus()))
                .toList();

        if (!existingPendingRequests.isEmpty()) {
            throw new IllegalArgumentException("A pending delivery change request already exists for this parcel.");
        }

        DeliveryChangeRequest request = DeliveryChangeRequest.builder()
                .customer(customer)
                .trackingNumber(dto.getTrackingNumber())
                .requestedDate(dto.getRequestedDate())
                .requestedSlot(dto.getRequestedSlot() != null ? dto.getRequestedSlot().name() : null)
                .status(DeliveryChangeStatus.REQUESTED)
                .build();

        request = deliveryRepository.save(request);


        String createUrl = UriComponentsBuilder.fromUriString(backendUrl)
                .path("/api/integration/parcels/{trackingNumber}/delivery-change")
                .buildAndExpand(dto.getTrackingNumber())
                .toUriString();

        Map<String, Object> caseRequestBody = Map.of(
                "date", dto.getRequestedDate() != null ? dto.getRequestedDate().toString() : null,
                "timeslot", dto.getRequestedSlot() != null ? dto.getRequestedSlot().name() : null
        );


        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(caseRequestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(createUrl, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();
        String caseNumber = responseBody != null ? (String) responseBody.get("case_number") : null;

        if (caseNumber != null) {
            request.setCaseNumber(caseNumber);
            request.setStatus(DeliveryChangeStatus.PENDING_REVIEW);
            request = deliveryRepository.save(request);
        }

        return request;
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
