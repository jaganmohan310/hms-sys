package com.pulsesquare.hms.service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${pulsesquare.jwt.secret:PulseSquareEnterpriseHMSSecretKey2026SecureKeyWithAtLeast256BitsLength!}")
    private String jwtSecret;

    @Value("${pulsesquare.jwt.expiration-ms:86400000}") // Default 24 hours
    private long jwtExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // Pad to ensure 256-bit key requirement for HS256
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            this.signingKey = Keys.hmacShaKeyFor(padded);
        } else {
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        }
    }

    public String generateToken(String username, String role, String fullName, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("fullName", fullName)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenWithClaims(String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate);

        if (claims != null) {
            claims.forEach(builder::claim);
        }

        return builder.signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Token has expired, but claims payload is authentic and needed for session teardown & audit logging
            return e.getClaims();
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("JWT token parsing failed: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}
