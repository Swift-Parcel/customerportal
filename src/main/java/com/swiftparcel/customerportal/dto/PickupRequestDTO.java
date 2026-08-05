package com.swiftparcel.customerportal.dto;

import com.swiftparcel.customerportal.model.enums.ServiceType;
import com.swiftparcel.customerportal.model.enums.TimeSlot;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PickupRequestDTO {
    @NotNull(message = "Preferred pickup date is required")
    @Future
    private LocalDate preferredPickupDate;
    @Max(value = 5000, message = "Declared value cannot exceed €5,000.")
    private float declaredValue;
    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int parcelHeight;
    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int parcelLength;
    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int parcelWidth;
    @Max(value = 30, message = "Maximum parcel weight: 30kg.")
    private float parcelWeight;
    @NotNull(message = "Preferred time slot is required")
    private TimeSlot preferredTimeSlot;
    @NotNull(message = "Recipient address is required")
    private Long recipientAddress;
    @NotNull(message = "Recipient name is required")
    private String recipientName;
    @NotNull(message = "Sender address is required")
    private Long senderAddress;
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;
}
