package com.swiftparcel.customerportal.dto;

import com.swiftparcel.customerportal.model.enums.ServiceType;
import com.swiftparcel.customerportal.model.enums.TimeSlot;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PickupRequestDTO {
    private LocalDate preferredPickupDate;
    private float declaredValue;
    private float parcelHeight;
    private float parcelLength;
    private float parcelWeight;
    private float parcelWidth;
    private TimeSlot preferredTimeSlot;
    private Long recipientAddress;
    private String recipientName;
    private Long customerId;
    private Long senderAddress;
    private ServiceType serviceType;
}
