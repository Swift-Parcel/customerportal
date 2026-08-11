package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeRequestOutcome;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryChangeDTO {
    @JsonProperty("case_number")
    @NotNull
    private String caseNumber;

    @JsonProperty("delivery_change_request_outcome")
    @NotNull
    private DeliveryChangeRequestOutcome outcome;
}
