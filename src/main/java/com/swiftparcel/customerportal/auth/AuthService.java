package com.swiftparcel.customerportal.auth;


import com.swiftparcel.customerportal.auth.dto.LoginRequest;
import com.swiftparcel.customerportal.auth.jwt.JwtService;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    @Value("${app.jwt.access-token-ttl}")
    private Duration accessTtl;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Customer customer = customerRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        return build(customer, refreshTokenService.issue(customer));
    }


    public AuthResponse refresh(String rawRefreshToken) {
        RotationResult result = refreshTokenService.rotate(rawRefreshToken);
        return build(result.customer(), result.refreshToken());
    }

    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    private AuthResponse build(Customer customer, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(jwtService.getToken(customer))
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTtl.toSeconds())
                .build();
    }
}
