package com.swiftparcel.customerportal.model;

import com.swiftparcel.customerportal.model.enums.CurrentStatus;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import com.swiftparcel.customerportal.model.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pickup_request")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 24)
    @Builder.Default
    CurrentStatus currentStatus = CurrentStatus.DRAFT;

    @Column(name = "sender_address_id", nullable = false)
    Long senderAddress;

    @Column(name = "accepted_quote_id")
    Long acceptedQuoteId;

    @Column(name = "recipient_name", nullable = false, length = 150)
    String recipientName;

    @Column(name = "recipient_address_id", nullable = false)
    Long recipientAddress;

    @Column(name = "weight_kg", nullable = false)
    float parcelWeight;

    @Column(name = "width_cm", nullable = false)
    int parcelWidth;

    @Column(name = "length_cm", nullable = false)
    int parcelLength;

    @Column(name = "height_cm", nullable = false)
    int parcelHeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 16)
    ServiceType serviceType;

    @Column(name = "declared_value_eur",precision = 10, scale = 2)
    BigDecimal declaredValue;

    @Column(name = "preferred_pickup_date", nullable = false)
    LocalDate preferredPickupDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_time_slot", nullable = false, length = 16)
    TimeSlot preferredTimeSlot;
}