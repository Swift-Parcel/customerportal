package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeliveryChangeResponseDTO {
    @JsonProperty("case_number")
    private String caseNumber;
}
