package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PricingRequestDTO {
    @JsonProperty("service_type")
    private ServiceType serviceType;
    private float weight;
    private int length;
    private int width;
    private int height;
    @JsonProperty("sender_address")
    private Address senderAddress;
    @JsonProperty("recipient_address")
    private Address recipientAddress;
}
