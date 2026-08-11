package com.swiftparcel.customerportal.dto.backOfficeForCustomer;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class BackofficeCustomerClient {

    private final RestTemplate restTemplate;

    @Value("${backoffice.api.base-url:http://localhost:3500}")
    private String backofficeBaseUrl;

    @Value("${app.backoffice.api-key}")
    private String apiKey;

    public void syncCustomerToBackOffice(BackofficeCustomerRequest request) {
        String url = backofficeBaseUrl + "/api/integration/customers";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<BackofficeCustomerRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForObject(url, entity, BackofficeCustomerResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync customer to Back-Office: " + e.getMessage(), e);
        }
    }
}
