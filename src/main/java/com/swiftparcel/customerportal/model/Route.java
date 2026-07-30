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

@Entity
@Table(name = "route")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class Route {

    @Id
    Long id;

    @Column(name = "route_type")
    String routeType;

    @Column(name = "multiplier")
    BigDecimal multiplier;
}
