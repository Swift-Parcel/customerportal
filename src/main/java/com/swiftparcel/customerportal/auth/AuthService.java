package com.swiftparcel.customerportal.auth;


import com.swiftparcel.customerportal.auth.dto.LoginRequest;
import com.swiftparcel.customerportal.auth.jwt.JwtService;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword() ));
        UserDetails user = customerRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
        String token = jwtService.getToken(user);

        return AuthResponse.builder()
                .token(token).build();
    }
}
