package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.enums.ParcelStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParcelStatusWebhookDTO {

    @NotBlank(message = "Tracking number is required")
    @Pattern(regexp = "^SP-[A-Z0-9]{8}$", message = "Tracking number must match SP-XXXXXXXX")
    @JsonProperty("tracking_number")
    private String trackingNumber;

    @NotNull(message = "Parcel status is required")
    @JsonProperty("parcel_status")
    private ParcelStatus parcelStatus;
}