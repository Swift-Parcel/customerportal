package com.swiftparcel.customerportal.backOffice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record BackofficeCustomerRequest(
        @JsonProperty("email") String email,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("preferred_language") String preferredLanguage
) {}