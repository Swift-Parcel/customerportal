package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PricingDTO {

    @JsonProperty("base_price")
    BigDecimal basePrice;
    @JsonProperty("weight_charge")
    BigDecimal weightCharge;
    @JsonProperty("surcharge")
    BigDecimal surcharge;
    @JsonProperty("zone_adjustment")
    BigDecimal zoneAdjustment;
    @JsonProperty("total_price")
    BigDecimal totalPrice;

}
