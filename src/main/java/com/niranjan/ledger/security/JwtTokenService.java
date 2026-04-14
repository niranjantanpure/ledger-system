package com.niranjan.ledger.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/**
 * Creates and validates signed JWT access tokens (HS256). Wire this from a login endpoint
 * and from a {@code OncePerRequestFilter} when you replace HTTP Basic with Bearer tokens.
 */
@Component
public class JwtTokenService {

    private static final String ISSUER = "ledger";

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenService(
            @Value("${jwt.secret:change-me-to-a-long-random-secret-32b-min!!}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("jwt.secret must be at least 32 characters for HS256");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public Optional<String> parseSubject(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public boolean isValid(String token) {
        return parseSubject(token).isPresent();
    }
}
