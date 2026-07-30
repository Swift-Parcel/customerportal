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
//    {
//        parcel_status: enum,
//        location: {
//            "facility" : "string ",
//                    " city " : " string ",
//                    " country_code ": " string ",
//                    "postal_code": "string",
//                    " lat": "double",
//                    "lon": "double"
//        },
//        tracking_history: [
//        {
//            "timestamp": "localDate",
//                "parcel_status": "enum",
//                " description": "string",
//                location: {
//            " facility": "string",
//                    " city":" string",
//                    "  country_code": "string",
//                    " postal_code":" string",
//                    "lat": "double",
//                    "lon": "double"
//        },
//
//        },
//    ]

//    private String trackingNumber;
//    private String status;

    @JsonProperty("tracking_history")
    private List<TrackingEvent> tracking_history;
    private Location location;


//    private LocalDate estimatedDeliveryDate;
//    private Party sender;
//    private Party recipient;
//    private Long weightKg;
//    private String serviceType;
//    private Long declaredValue;
}

@Data
@Builder
@NoArgsConstructor  // <--- ADD THIS
@AllArgsConstructor
class TrackingEvent {

    private LocalDateTime timeStamp;
    private enum parcel_status {};
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
@NoArgsConstructor  // <--- ADD THIS
@AllArgsConstructor
class Party {
    private String fullName;
    private String email;
}

