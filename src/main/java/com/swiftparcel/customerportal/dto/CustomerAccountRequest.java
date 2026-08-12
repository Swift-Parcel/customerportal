package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.processing.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAccountRequest {


    @JsonProperty("email")
    String email;
    @JsonProperty("full_name")
    String fullName;
    @JsonProperty("phone_number")
    String phoneNumber;
    @JsonProperty("password")
    String password;
    @JsonProperty("preferred_language")
    String preferredLanguage;
    @JsonProperty("default_address")
    AddressDTO defaultAddress;



}
