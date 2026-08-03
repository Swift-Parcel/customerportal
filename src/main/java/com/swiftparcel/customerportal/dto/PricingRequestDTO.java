package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PricingRequestDTO {
    @JsonProperty("service_type")
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @Min(value = 0, message = "Weight must be positive")
    @Max(value = 30, message = "Maximum parcel weight: 30kg.")
    @NotNull
    @DecimalMin(value = "0.001", message = "Weight must be greater than zero")
    @DecimalMax(value = "1000.000")
    @Digits(integer = 4, fraction = 3)
    private BigDecimal weight;


    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    @Min(value = 1, message = "Minimum single dimension: 1cm")
    private int length;

    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    @Min(value = 1, message = "Minimum single dimension: 1cm")
    private int width;

    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    @Min(value = 1, message = "Minimum single dimension: 1cm")
    private int height;

    @JsonProperty("sender_address")
    @NotNull(message = "Sender address is required")
    private Address senderAddress;

    @JsonProperty("recipient_address")
    @NotNull(message = "Recipient address is required")
    private Address recipientAddress;
}
