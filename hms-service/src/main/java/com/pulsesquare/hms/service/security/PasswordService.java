package com.pulsesquare.hms.service.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder(10);
    }

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encode(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Raw password cannot be null");
        }
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        // Direct BCrypt match
        if (passwordEncoder.matches(rawPassword, encodedPassword)) {
            return true;
        }
        // Fallback for plain-text legacy entries during migration
        return rawPassword.equals(encodedPassword);
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
