package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeAddNoteRequest;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseRequest;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseResponse;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackOfficeIntegrationService {

    private final RestTemplate restTemplate;

    @Value("${backoffice.api.base-url:http://localhost:3500}")
    private String backOfficeBaseUrl;

    @Value("${backoffice.api.base-url:http://localhost:3500}")
    private String bearerToken;

    public String sendCaseToBackOffice(BackOfficeCaseRequest request) {
        String url = backOfficeBaseUrl + "/api/integration/cases";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.setBearerAuth(bearerToken);

        HttpEntity<BackOfficeCaseRequest> entity = new HttpEntity<>(request, headers);

        try {
            BackOfficeCaseResponse response = restTemplate.postForObject(url, entity, BackOfficeCaseResponse.class);

            if (response != null && response.getCaseNumber() != null) {
                return response.getCaseNumber();
            }
            throw new RuntimeException("Failed to receive case number from Back-Office");
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with Back-Office C# service: " + e.getMessage(), e);
        }
    }
    public void addCaseNote(String caseNumber, String customerEmail, String message) {
        String url = backOfficeBaseUrl + "/api/integration/cases/" + caseNumber + "/notes";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken);

        BackOfficeAddNoteRequest request = BackOfficeAddNoteRequest.builder()
                .customerEmail(customerEmail)
                .message(message)
                .build();

        HttpEntity<BackOfficeAddNoteRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with Back-Office C# service: " + e.getMessage(), e);
        }
    }

    public BackOfficeCaseStatusResponse getCaseStatus(String caseNumber) {
        String url = backOfficeBaseUrl + "/api/integration/cases/" + caseNumber + "/status";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<BackOfficeCaseStatusResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, BackOfficeCaseStatusResponse.class);
            if (response.getBody() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Received empty response from Back-Office for case status");
            }
            return response.getBody();
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with Back-Office C# service: " + e.getMessage(), e);
        }
    }
}
