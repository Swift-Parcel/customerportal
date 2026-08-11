package com.swiftparcel.customerportal.dto.integrationComplainCase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackOfficeAddNoteRequest {
    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("message")
    private String message;
}
