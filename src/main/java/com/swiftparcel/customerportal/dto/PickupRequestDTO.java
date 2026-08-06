package com.swiftparcel.customerportal.dto;

import com.swiftparcel.customerportal.model.enums.ServiceType;
import com.swiftparcel.customerportal.model.enums.TimeSlot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @FutureOrPresent(message = "Invalid date in the past")
    private LocalDate preferredPickupDate;
    @DecimalMax(value = "5000", message = "Declared value cannot exceed €5,000.")
    private float declaredValue;
    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int parcelHeight;
    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int parcelLength;
    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int parcelWidth;
    @DecimalMax(value = "30", message = "Maximum parcel weight: 30kg.")
    private float parcelWeight;
    @NotNull(message = "Preferred time slot is required")
    private TimeSlot preferredTimeSlot;

    @NotNull(message = "Recipient address is required")
    private AddressDTO recipientAddress;
    
    @NotNull(message = "Recipient name is required")
    @Valid
    private String recipientName;
    
    @NotNull(message = "Sender address is required")
    @Valid
    private AddressDTO senderAddress;
    
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;
}
