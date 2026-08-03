package com.swiftparcel.customerportal.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "quote")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "pickup_request_id", nullable = false)
    Long pickupRequestId;

    @Column(name = "base_price_eur", nullable = false, precision = 10, scale = 2)
    BigDecimal basePrice;

    @Column(name = "weight_charge_eur", nullable = false, precision = 10, scale = 2)
    BigDecimal weightCharge;

    @Column(name = "surcharge_eur", nullable = false, precision = 10, scale = 2)
    BigDecimal surcharge;

    @Column(name = "zone_adjustment_eur", nullable = false, precision = 10, scale = 2)
    BigDecimal zoneAdjustment;

    @Column(name = "total_price_eur", nullable = false, precision = 10, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "quote_route_type", nullable = false, length = 32)
    String quoteRouteType;

    @Column(name = "quoted_at", nullable = false)
    @Builder.Default
    Instant quotedAt = Instant.now();

    @Column(name = "quote_expires_at", nullable = false)
    Instant quoteExpiresAt;
}