package com.swiftparcel.customerportal.model;


import com.swiftparcel.customerportal.model.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "service_rate")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServiceRate {

    @Id
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    ServiceType serviceType;

    @Column(name =  "base_price")
    BigDecimal basePrice;

    @Column(name = "per_kg_rate")
    BigDecimal perKgRate;

    @Column(name = "surcharge_amount")
    BigDecimal surchargeAmount;

    @Column(name = "surcharge_weight_threshold_kg")
    BigDecimal surchargeWeightThresholdKg;

    @Column(name = "order_cut_off_time")
    LocalTime orderCutOffTime;

    @Column(name = "min_time_before_slot_minutes")
    Integer minTimeBeforeSlotMinutes;

    @Column(name = "cross_country_allowed")
    boolean crossCountryAllowed;
}
