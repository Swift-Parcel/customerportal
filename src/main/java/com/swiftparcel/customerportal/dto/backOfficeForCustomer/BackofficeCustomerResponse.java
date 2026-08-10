package com.swiftparcel.customerportal.dto.backOfficeForCustomer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BackofficeCustomerResponse(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("status") String status,
        @JsonProperty("message") String message
) {}