package com.swiftparcel.customerportal.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BackOfficeCaseResponse {

    @JsonProperty("case_number")
    private String caseNumber;

    private String message;
}