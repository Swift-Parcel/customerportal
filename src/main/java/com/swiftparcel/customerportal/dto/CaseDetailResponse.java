package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.enums.CaseStatus;
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
public class CaseDetailResponse {
    @JsonProperty("case_number")
    private String caseNumber;

    @JsonProperty("case_type")
    private CaseType caseType;

    @JsonProperty("tracking_numbers")
    private List<String> trackingNumbers;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private CaseStatus status;

    @JsonProperty("resolution")
    private String resolution;

    @JsonProperty("notes")
    private List<CaseNoteResponse> notes;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
