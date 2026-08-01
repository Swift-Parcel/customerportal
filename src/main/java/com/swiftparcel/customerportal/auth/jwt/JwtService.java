package com.swiftparcel.customerportal.auth.jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-token-ttl}")
    private Duration accessTtl;


    public String getToken(UserDetails user){
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user){
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(accessTtl))
                .id(UUID.randomUUID().toString())
                .claims(c -> c.putAll(extraClaims))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String getUsernameFromToken(String token){
        return decoder.decode(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user){
        try{
            Jwt decodedToken = decoder.decode(token);
            return decodedToken.getSubject().equals(user.getUsername()) &&
                    decodedToken.getExpiresAt() != null &&
                    decodedToken.getExpiresAt().isAfter(Instant.now());
        }catch (JwtException e){
            return false;
        }
    }


}
