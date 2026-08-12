package com.swiftparcel.customerportal.auth.service;

import com.swiftparcel.customerportal.auth.dto.RotationResult;
import com.swiftparcel.customerportal.auth.model.RefreshToken;
import com.swiftparcel.customerportal.model.*;
import com.swiftparcel.customerportal.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${app.jwt.refresh-token-ttl}")
    private Duration refreshTtl;

    //
    @Transactional
    public String issue(Customer customer) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        repository.save(RefreshToken.builder()
                .customer(customer)
                .tokenHash(hash(raw))
                .expiresAt(Instant.now().plus(refreshTtl))
                .build());

        return raw;
    }

    // more like a check of the token, it also returns a new one if
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public RotationResult rotate(String rawToken) {
        RefreshToken stored = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));

        if (stored.isInvalidated()) {
            repository.revokeAllForCustomer(stored.getCustomer().getEmail(), Instant.now());
            throw new BadCredentialsException("Refresh token already used; all sessions revoked");
        }
        if (stored.isExpired()) {
            throw new BadCredentialsException("Refresh token expired");
        }
        stored.setRevokedAt(Instant.now());
        Customer customer = stored.getCustomer();
        return new RotationResult(customer, issue(customer));
    }

    @Transactional
    public void revoke(String rawToken) {
        repository.findByTokenHash(hash(rawToken))
                .filter(RefreshToken::isActive)
                .ifPresent(t -> t.setRevokedAt(Instant.now()));
       // if nothing happens it's ok
    }

    private String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}