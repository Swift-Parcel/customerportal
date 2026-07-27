package com.swiftparcel.customerportal.auth.repository;

import com.swiftparcel.customerportal.auth.domain.RefreshToken;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revokedAt = :now " +
            "WHERE r.customer.email = :email AND r.revokedAt IS NULL")
    int revokeAllForCustomer(@Param("email") String email, @Param("now") Instant now);

}
