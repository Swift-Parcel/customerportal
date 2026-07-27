package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String email;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("preferred_language")
    private String preferredLanguage;
    @JsonProperty("default_address")
    private AddressDTO defaultAddress;
}
