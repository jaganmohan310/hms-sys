package com.pulsesquare.hms.model;

import com.pulsesquare.hms.model.dto.AuthResponse;
import com.pulsesquare.hms.model.dto.LoginRequest;
import com.pulsesquare.hms.model.dto.SignupRequest;
import com.pulsesquare.hms.model.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest("dr_sharma", "doctor123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankLoginRequest() {
        LoginRequest request = new LoginRequest("", "");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    @Test
    void testValidSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("dr_koushikkolla");
        request.setPassword("Password@2026");
        request.setEmail("koushikkolla51@gmail.com");
        request.setFullName("Dr. KOUSHIK KOLLA");
        request.setRole("DOCTOR");
        request.setDepartment("Cardiology");
        request.setContactNumber("+917483377571");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmailSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("dr_koushikkolla");
        request.setPassword("Password@2026");
        request.setEmail("not-an-email");
        request.setFullName("Dr. KOUSHIK KOLLA");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testAuthResponseBuilder() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("dr_koushikkolla");
        userDto.setEmail("koushikkolla51@gmail.com");
        userDto.setFullName("Dr. KOUSHIK KOLLA");
        userDto.setRole("DOCTOR");

        AuthResponse authResponse = AuthResponse.success("Login successful", "JWT_SAMPLE_TOKEN", 86400000L, userDto);
        assertTrue(authResponse.isSuccess());
        assertEquals("JWT_SAMPLE_TOKEN", authResponse.getToken());
        assertEquals("dr_koushikkolla", authResponse.getUsername());
        assertEquals("DOCTOR", authResponse.getRole());
        assertEquals(1L, authResponse.getUser().getId());
    }
}
