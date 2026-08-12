package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.enums.CaseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCaseRequest {
    @NotEmpty(message = "At least one tracking number is required")
    @JsonProperty("tracking_numbers")
    private List<
            @Pattern(regexp = "^SP-[A-Za-z0-9]{8}$", message = "Tracking number must follow SP-XXXXXXXX format")
                    String
            > trackingNumbers;

    @NotNull(message = "Case type is required")
    @JsonProperty("case_type")
    private CaseType caseType;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Title is required")
    private String title;
}
