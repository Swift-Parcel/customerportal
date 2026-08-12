package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeAddNoteRequest;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseRequest;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseResponse;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseStatusResponse;
import com.swiftparcel.customerportal.dto.*;
import com.swiftparcel.customerportal.dto.backOfficeForCustomer.BackofficeCustomerRequest;
import com.swiftparcel.customerportal.dto.backOfficeForCustomer.BackofficeCustomerResponse;
import com.swiftparcel.customerportal.dto.integrationComplainCase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackOfficeIntegrationService {

    private final RestTemplate restTemplate;

    @Value("${app.backoffice.base-url}")
    private String backOfficeBaseUrl;

    @Value("${app.backoffice.api-key}")
    private String apiKey;

    // --- Parcel Integrations ---

    public String fetchParcelStatus(String trackingNumber) {
        String url = UriComponentsBuilder.fromUriString(backOfficeBaseUrl)
                .path("/api/integration/parcels/{trackingNumber}")
                .buildAndExpand(trackingNumber)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ParcelStatusDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ParcelStatusDTO.class);
            if (response.getBody() != null) {
                return response.getBody().getParcelStatus();
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Received empty response from back-office.");
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Back-office is unreachable: " + e.getMessage());
        }
    }

    public String callBackendForDeliveryChange(String trackingNumber, Map<String, Object> caseRequestBody) {
        String createUrl = UriComponentsBuilder.fromUriString(backOfficeBaseUrl)
                .path("/api/integration/parcels/{trackingNumber}/delivery-change")
                .buildAndExpand(trackingNumber)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(caseRequestBody, headers);

        try {
            DeliveryChangeResponseDTO response = restTemplate.postForObject(createUrl, entity, DeliveryChangeResponseDTO.class);
            return response != null ? response.getCaseNumber() : null;
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with back-office: " + e.getMessage());
        }
    }

    public List<ParcelDTO> getCustomerParcels(String customerEmail) {
        String url = UriComponentsBuilder.fromUriString(backOfficeBaseUrl)
                .path("/api/integration/parcels")
                .queryParam("customerEmail", customerEmail)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ParcelResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ParcelResponseDTO.class);
            if (response.getBody() == null || response.getBody().getParcels() == null) {
                return Collections.emptyList();
            }
            return response.getBody().getParcels();
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with back-office: " + e.getMessage());
        }
    }

    public ParcelDetailResponse getParcelDetails(String trackingNumber) {
        String url = UriComponentsBuilder.fromUriString(backOfficeBaseUrl)
                .path("/api/integration/parcels")
                .queryParam("trackingNumber", trackingNumber)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ParcelDetailResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ParcelDetailResponse.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with back-office: " + e.getMessage());
        }
    }

    public ScheduleResponse getSchedule(String trackingNumber) {
        String url = UriComponentsBuilder.fromUriString(backOfficeBaseUrl)
                .path("/api/integration/parcels")
                .queryParam("trackingNumber", trackingNumber)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ScheduleResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ScheduleResponse.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with back-office: " + e.getMessage());
        }
    }

    public void confirmDelivery(String trackingNumber, String customerEmail) {
        String url = UriComponentsBuilder.fromUriString(backOfficeBaseUrl)
                .path("/api/integration/parcels/{trackingNumber}/confirm-delivery")
                .buildAndExpand(trackingNumber)
                .toUriString();

        Map<String, String> body = Map.of("customer_email", customerEmail);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PATCH, entity, Void.class);
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with back-office: " + e.getMessage());
        }
    }

    public ApiResponse changeDelivery(String trackingNumber, ChangeDeliveryDTO changeDeliveryDTO) {
        String url = UriComponentsBuilder.fromUriString(backOfficeBaseUrl)
                .path("/api/integration/parcels/{trackingNumber}/delivery-change")
                .buildAndExpand(trackingNumber)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<ChangeDeliveryDTO> entity = new HttpEntity<>(changeDeliveryDTO, headers);

        try {
            ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, ApiResponse.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with back-office: " + e.getMessage());
        }
    }

    public String sendCaseToBackOffice(BackOfficeCaseRequest request) {
        String url = backOfficeBaseUrl + "/api/integration/cases";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<BackOfficeCaseRequest> entity = new HttpEntity<>(request, headers);

        try {
            BackOfficeCaseResponse response = restTemplate.postForObject(url, entity, BackOfficeCaseResponse.class);

            if (response != null && response.getCaseNumber() != null) {
                return response.getCaseNumber();
            }
            throw new RuntimeException("Failed to receive case number from Back-Office");
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with Back-Office C# service: " + e.getMessage(), e);
        }
    }

    public BackOfficeCaseStatusResponse getCaseStatus(String caseNumber) {
        String url = backOfficeBaseUrl + "/api/integration/cases/" + caseNumber + "/status";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<BackOfficeCaseStatusResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, BackOfficeCaseStatusResponse.class);
            if (response.getBody() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Received empty response from Back-Office for case status");
            }
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with Back-Office C# service: " + e.getMessage(), e);
        }
    }

    public void addCaseNote(String caseNumber, String customerEmail, String message) {
        String url = backOfficeBaseUrl + "/api/integration/cases/" + caseNumber + "/notes";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        BackOfficeAddNoteRequest request = BackOfficeAddNoteRequest.builder()
                .customerEmail(customerEmail)
                .message(message)
                .build();

        HttpEntity<BackOfficeAddNoteRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with Back-Office C# service: " + e.getMessage(), e);
        }
    }

    public void syncCustomerToBackOffice(BackofficeCustomerRequest request) {
        String url = backOfficeBaseUrl + "/api/integration/customers";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<BackofficeCustomerRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForObject(url, entity, BackofficeCustomerResponse.class);
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync customer to Back-Office: " + e.getMessage(), e);
        }
    }
}
