package com.swiftparcel.customerportal.backOffice;


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

    @Value("${backoffice.api.bearer-token:secret}")
    private String bearerToken;

    public void syncCustomerToBackOffice(BackofficeCustomerRequest request) {
        String url = backofficeBaseUrl + "/api/integration/customers";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken);

        HttpEntity<BackofficeCustomerRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForObject(url, entity, BackofficeCustomerResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync customer to Back-Office: " + e.getMessage(), e);
        }
    }
}
