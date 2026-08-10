package com.swiftparcel.customerportal.dto.integrationComplainCase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackOfficeCaseRequest {

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("tracking_numbers")
    private List<String> trackingNumbers;

    @JsonProperty("case_type")
    private String caseType;

    private String description;
}