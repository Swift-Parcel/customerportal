package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.enums.CaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseResponse {
    @JsonProperty("case_number")
    private String caseNumber;

    @JsonProperty("tracking_numbers")
    private List<String> trackingNumbers;

    @JsonProperty("case_type")
    private CaseType caseType;

    private String description;

//    private String channel;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
