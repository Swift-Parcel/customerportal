package com.swiftparcel.customerportal.backOffice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackofficeCustomerClient {
//    private final WebClient backofficeWebClient;
//
//    @Value("${backoffice.timeout-ms}")
//    private long timeoutMs;
//
//    public BackofficeCustomerResponse createCustomer(BackofficeCustomerRequest request) {
//        try {
//            return backofficeWebClient.post()
//                    .uri("/api/customers")
//                    .bodyValue(request)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, resp ->
//                            resp.bodyToMono(String.class).defaultIfEmpty("")
//                                    .map(body -> new BackofficeSyncException(
//                                            "Backoffice error " + resp.statusCode())))
//                    .bodyToMono(BackofficeCustomerResponse.class)
//                    .timeout(Duration.ofMillis(timeoutMs))
//                    .block();
//        } catch (BackofficeSyncException e) {
//            throw e;
//        } catch (Exception e) {
//            log.error("Backoffice call failed for {}", request.email(), e);
//            throw new BackofficeSyncException("Backoffice unreachable", e);
//        }
//    }

}
