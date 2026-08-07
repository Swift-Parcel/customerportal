package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeRequestOutcome;
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
    private String caseNumber;

    @JsonProperty("delivery_change_request_outcome")
    private DeliveryChangeRequestOutcome outcome;
}
