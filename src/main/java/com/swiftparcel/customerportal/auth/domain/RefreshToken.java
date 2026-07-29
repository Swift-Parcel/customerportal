package com.swiftparcel.customerportal.auth.domain;

import com.swiftparcel.customerportal.model.Customer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.Instant;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString(exclude = "customer")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_email", referencedColumnName = "email", nullable = false)
    Customer customer;

    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    String tokenHash;

    @Column(name = "expires_at", nullable = false)
    Instant expiresAt;

    @Column(name = "revoked_at")
    Instant revokedAt;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    Instant createdAt = Instant.now();

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public boolean isInvalidated() {
        return revokedAt != null;
    }

    public boolean isActive() {
        return !isExpired() && !isInvalidated();
    }
}