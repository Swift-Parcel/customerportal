package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmQuoteResponse {

    private Long pickupRequestId;

    private Long quoteId;

    private BigDecimal totalPriceEur;

    private String trackingNumber;

    private String status;

    private Instant paidAt;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BackofficeResponse {

        @JsonProperty("tracking_number")
        private String trackingNumber;

        @JsonProperty("parcel_status")
        private String parcelStatus;
    }
}