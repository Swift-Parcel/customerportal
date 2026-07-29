package com.swiftparcel.customerportal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ScheduleResponse {
    private String trackingNumber;
    private String status;
    private LocalDate estimatedDeliveryDate;
    private LocalTime slotStart;
    private LocalTime slotEnd;

}
