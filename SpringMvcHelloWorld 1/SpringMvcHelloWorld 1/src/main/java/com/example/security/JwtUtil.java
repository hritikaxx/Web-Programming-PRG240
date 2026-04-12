package com.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility for creating and validating JWT tokens.
 *
 * Uses HS256 with a fixed secret. In production, load the secret from an
 * environment variable or external config rather than hardcoding it.
 */
@Component
public class JwtUtil {

    // Secret must be at least 256 bits (32 bytes) for HS256.
    private static final String SECRET = "spring-mvc-hello-world-jwt-secret-key-please-change-me-32+chars";
    private static final long EXPIRATION_MS = 5 * 60 * 1000L; // 5 minutes

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /** Generate a JWT for the given username. */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Parse and validate a token. Returns the claims on success.
     * Throws ExpiredJwtException if expired, JwtException if otherwise invalid.
     */
    public Claims validateToken(String token) throws ExpiredJwtException, JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
