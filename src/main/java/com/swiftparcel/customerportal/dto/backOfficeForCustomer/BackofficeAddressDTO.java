package com.swiftparcel.customerportal.dto.backOfficeForCustomer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record BackofficeAddressDTO(
        @JsonProperty("city") String city,
        @JsonProperty("country_code") String countryCode,
        @JsonProperty("postal_code") String postalCode,
        @JsonProperty("street") String street,
        @JsonProperty("street_number") String streetNumber
) {}
