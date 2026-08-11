package com.swiftparcel.customerportal.dto.integrationComplainCase;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.enums.CaseStatus;
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
public class BackOfficeCaseStatusResponse {
    @JsonProperty("case_status")
    private CaseStatus caseStatus;

    @JsonProperty("notes")
    private List<BackOfficeNote> notes;

    @JsonProperty("resolution")
    private String resolution;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BackOfficeNote {
        private LocalDateTime timestamp;
        private String note;
    }
}
