package com.pulsesquare.hms.service;

import com.pulsesquare.hms.service.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Hashing & Verification Service Tests")
class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    @DisplayName("Should encode password into standard BCrypt format")
    void testEncodePassword() {
        String raw = "DoctorSecret@123";
        String encoded = passwordService.encode(raw);

        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$10$") || encoded.startsWith("$2b$10$"), "Must use BCrypt cost factor 10");
        assertNotEquals(raw, encoded);
    }

    @Test
    @DisplayName("Should match correct password and reject incorrect password")
    void testPasswordMatches() {
        String raw = "AdminPass#2026";
        String encoded = passwordService.encode(raw);

        assertTrue(passwordService.matches(raw, encoded));
        assertFalse(passwordService.matches("WrongPass", encoded));
        assertFalse(passwordService.matches(null, encoded));
        assertFalse(passwordService.matches(raw, null));
    }
}
