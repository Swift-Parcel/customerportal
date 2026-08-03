package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PricingRequestDTO {
    @JsonProperty("service_type")
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @Min(value = 0, message = "Weight must be positive")
    @Max(value = 30, message = "Maximum parcel weight: 30kg.")
    private float weight;

    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int length;

    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int width;

    @Max(value = 120, message = "Maximum single dimension: 120cm.")
    private int height;

    @JsonProperty("sender_address")
    @NotNull(message = "Sender address is required")
    private Address senderAddress;

    @JsonProperty("recipient_address")
    @NotNull(message = "Recipient address is required")
    private Address recipientAddress;
}
