package com.swiftparcel.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    private String trackingNumber;
    private String status;
    private LocalDate estimatedDeliveryDate;
    private LocalTime slotStart;
    private LocalTime slotEnd;

}
