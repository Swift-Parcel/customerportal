package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParcelStatusUpdateWebhookDTO {

    @JsonProperty("tracking_number")
    String trackingNumber;

    @JsonProperty("parcel_status")
    String parcelStatus;

}
