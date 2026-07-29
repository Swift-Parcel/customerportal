package com.swiftparcel.customerportal.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

public class ParcelDetailResponse {
    private String trackingNumber;
    private String status;
    private List<TrackingEvent> trackingHistory;
    private LocalDate estimatedDeliveryDate;
    private Party sender;
    private Party recipient;
    private BigDecimal weightKg;
    private String serviceType;
    private Long declaredValue;
}

@Data
@Builder
class TrackingEvent {
    private String status;
    private OffsetDateTime timestamp;
}

@Data
@Builder
class Party {
    private String fullName;
    private String email;
}

