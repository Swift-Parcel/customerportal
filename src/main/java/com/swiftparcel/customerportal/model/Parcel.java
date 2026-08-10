package com.swiftparcel.customerportal.model;

import com.swiftparcel.customerportal.model.enums.ParcelStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "parcel")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name ="customer_id")
    Long customer;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 11)
    String trackingNumber;

    @Column(name = "pickup_request_id")
    Long pickupRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    ParcelStatus status;

    @UpdateTimestamp
    @Column(name = "status_updated_at", nullable = false)
    LocalDateTime updatedAt;
}