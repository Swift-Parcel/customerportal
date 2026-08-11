package com.swiftparcel.customerportal.dto.backOfficeForCustomer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record BackofficeCustomerRequest(
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("phone") String phone,
        @JsonProperty("address") BackofficeAddressDTO address,
        @JsonProperty("preferred_language") String preferredLanguage
) {}