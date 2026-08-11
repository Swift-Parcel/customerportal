package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ParcelStatusDTO {
    @JsonProperty("parcel_status")
    private String parcelStatus;
}
