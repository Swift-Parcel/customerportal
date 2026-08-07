package com.swiftparcel.customerportal.model;

import com.swiftparcel.customerportal.model.enums.DeliveryChangeStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_change_request")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @Column(name = "tracking_number", nullable = false, length = 11)
    String trackingNumber;

    @Column(name = "case_number", length = 32)
    String caseNumber;

    @Column(name = "requested_date")
    LocalDate requestedDate;

    @Column(name = "requested_slot", length = 16)
    String requestedSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    DeliveryChangeStatus status = DeliveryChangeStatus.REQUESTED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;
}
