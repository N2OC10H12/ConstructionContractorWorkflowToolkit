package com.company.ConstructionContractorWorkflowToolkit.auth.service;

import com.company.ConstructionContractorWorkflowToolkit.user.entity.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // TEMP for MVP. Later move to application-local.yml / env variable.
    private static final String SECRET =
            "pmworkflow-local-dev-secret-key-must-be-at-least-32-bytes-long";

    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 8; // 8 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(AppUser user) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(getSigningKey())
                .compact();
    }
}