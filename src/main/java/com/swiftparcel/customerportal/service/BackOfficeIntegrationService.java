package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseRequest;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BackOfficeIntegrationService {

    private final RestTemplate restTemplate;

    @Value("${backoffice.api.base-url:http://localhost:3500}")
    private String backOfficeBaseUrl;


    @Value("${app.backoffice.api-key}")
    private String apiKey;

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
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with Back-Office C# service: " + e.getMessage(), e);
        }
    }
}
