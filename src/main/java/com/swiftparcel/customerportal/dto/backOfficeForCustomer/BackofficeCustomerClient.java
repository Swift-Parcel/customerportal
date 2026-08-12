package com.swiftparcel.customerportal.dto.backOfficeForCustomer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Slf4j
@Component
@RequiredArgsConstructor
public class BackofficeCustomerClient {

    private final RestTemplate restTemplate;

    @Value("${app.backoffice.base-url}")
    private String backofficeBaseUrl;

    @Value("${app.backoffice.api-key}")
    private String apiKey;

    public void syncCustomerToBackOffice(BackofficeCustomerRequest request) {
        log.info("[DEBUG_LOG] Injected backofficeBaseUrl: '{}'", backofficeBaseUrl);
        String url = backofficeBaseUrl + "/api/integration/customers";
        log.info("[DEBUG_LOG] Attempting to sync customer to Back-Office. URL: {}", url);
        log.info("[DEBUG_LOG] Request data: {}", request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<BackofficeCustomerRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForObject(url, entity, BackofficeCustomerResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync customer to Back-Office: " + e);
        }
    }
}
