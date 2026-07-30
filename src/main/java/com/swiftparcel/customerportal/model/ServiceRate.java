package com.swiftparcel.customerportal.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "service_rate")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ServiceRate {

    @Id
    Long id;

    @Column(name = "service_type")
    String serviceType;

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
    int minTimeBeforeSlotMinutes;

    @Column(name = "cross_country_allowed")
    boolean crossCountryAllowed;
}
