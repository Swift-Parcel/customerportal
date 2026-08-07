package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.enums.TimeSlot;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryChangeRequestDTO {

    @NotBlank(message = "Tracking number is required")
    @JsonProperty("tracking_number")
    private String trackingNumber;

    @NotNull(message = "Requested date is required")
    @Future(message = "Requested date must be in the future")
    @JsonProperty("requested_date")
    private LocalDate requestedDate;

    @NotNull(message = "Requested slot is required")
    @JsonProperty("requested_slot")
    private TimeSlot requestedSlot;
}
