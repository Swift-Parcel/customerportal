package com.swiftparcel.customerportal.model;

import com.swiftparcel.customerportal.model.enums.CurrentStatus;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import com.swiftparcel.customerportal.model.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "pickup_request")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 24)
    CurrentStatus currentStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_address_id", nullable = false)
    Address senderAddress;

    @Column(name = "recipient_name", nullable = false, length = 150)
    String recipientName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_address_id", nullable = false)
    Address recipientAddress;

    @Column(name = "weight_kg", nullable = false)
    float parcelWeight;

    @Column(name = "width_cm", nullable = false)
    float parcelWidth;

    @Column(name = "length_cm", nullable = false)
    float parcelLength;

    @Column(name = "height_cm", nullable = false)
    float parcelHeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 16)
    ServiceType serviceType;

    @Column(name = "declared_value_eur", nullable = false)
    float declaredValue;

    @Column(name = "quoted_price_eur")
    float quotedPrice;

    @Column(name = "preferred_pickup_date", nullable = false)
    LocalDate preferredPickupDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_time_slot", nullable = false, length = 16)
    TimeSlot preferredTimeSlot;
}