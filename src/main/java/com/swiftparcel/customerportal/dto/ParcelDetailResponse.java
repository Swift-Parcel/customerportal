package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ParcelDetailResponse {

    @JsonProperty("tracking_history")
    private List<TrackingEvent> tracking_history;
    private Location location;

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TrackingEvent {

    private LocalDateTime timeStamp;

    private enum parcel_status {}

    ;
    private String description;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Location {
    @JsonProperty("postal_code")
    private String postal_code;
    private String city;
    @JsonProperty("country_code")
    private String country_code;
    private Double lat;
    private Double lon;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Party {
    private String fullName;
    private String email;
}
