package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BackOfficeParcelDTO {

    private Sender sender;

    private Recipient recipient;

    private Parcel parcel;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Sender {
        private String email;

        @JsonProperty("sender_address")
        private BackofficeAddress senderAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Recipient {
        private String name;

        @JsonProperty("recipient_address")
        private BackofficeAddress recipientAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BackofficeAddress {
        private String city;

        @JsonProperty("country_code")
        private String countryCode;

        @JsonProperty("postal_code")
        private String postalCode;

        private String street;

        @JsonProperty("street_number")
        private String streetNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Parcel {
        private BigDecimal weight;

        private Integer height;

        private Integer width;

        private Integer length;

        @JsonProperty("service_type")
        private String serviceType;

        @JsonProperty("declared_value")
        private BigDecimal declaredValue;

        @JsonProperty("preferred_pickup_date")
        private String preferredPickupDate;

        @JsonProperty("preferred_pickup_timeslot")
        private String preferredPickupTimeslot;
    }
}