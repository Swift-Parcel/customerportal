package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDTO {
    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("parcel_status")
    private String status;

    private SenderDTO sender;

    private RecipientDTO recipient;

    @JsonProperty("created_date")
    private LocalDateTime createdDate;

    @JsonProperty("service_type")
    private String serviceType;
}
