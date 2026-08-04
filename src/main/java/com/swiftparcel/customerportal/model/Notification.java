package com.swiftparcel.customerportal.model;

import com.swiftparcel.customerportal.model.enums.NotificationEventType;
import com.swiftparcel.customerportal.model.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    NotificationEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NotificationStatus status;

    String message;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}
