package com.pulsesquare.hms.service;

import com.pulsesquare.hms.service.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stateless JWT Token Provider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "PulseSquareEnterpriseHMSSecretKey2026SecureKeyWithAtLeast256BitsLength!");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 3600000L);
        tokenProvider.init();
    }

    @Test
    @DisplayName("Should generate valid JWT token with claims and parse subject and role")
    void testTokenGenerationAndValidation() {
        String token = tokenProvider.generateToken("dr_rajesh", "DOCTOR", "Dr. Rajesh Sharma", "dr.rajesh@pulsesquare.com");

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("dr_rajesh", tokenProvider.getUsernameFromToken(token));
        assertEquals("DOCTOR", tokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Should reject tampered or invalid token")
    void testInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid.token.payload"));
        assertFalse(tokenProvider.validateToken(""));
    }
}
