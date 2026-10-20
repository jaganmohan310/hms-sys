package com.pulsesquare.hms.service;

import com.pulsesquare.hms.common.exception.DuplicateResourceException;
import com.pulsesquare.hms.jooq.tables.pojos.HmsRoles;
import com.pulsesquare.hms.jooq.tables.pojos.HmsUsers;
import com.pulsesquare.hms.model.dto.AuthResponse;
import com.pulsesquare.hms.model.dto.LoginRequest;
import com.pulsesquare.hms.model.dto.SignupRequest;
import com.pulsesquare.hms.model.dto.UserDto;
import com.pulsesquare.hms.repo.repository.RoleRepository;
import com.pulsesquare.hms.repo.repository.UserRepository;
import com.pulsesquare.hms.service.impl.AuthServiceImpl;
import com.pulsesquare.hms.service.security.JwtTokenProvider;
import com.pulsesquare.hms.service.security.PasswordService;
import com.pulsesquare.hms.service.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Business Logic Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthServiceImpl authService;

    private HmsUsers sampleUser;
    private Set<HmsRoles> sampleRoles;

    @BeforeEach
    void setUp() {
        HmsRoles doctorRole = new HmsRoles();
        doctorRole.setId(1L);
        doctorRole.setName("ROLE_DOCTOR");
        doctorRole.setDescription("Doctor Role");

        sampleRoles = new HashSet<>(Collections.singletonList(doctorRole));

        sampleUser = new HmsUsers();
        sampleUser.setId(1L);
        sampleUser.setUsername("dr_rajesh");
        sampleUser.setEmail("dr.rajesh@pulsesquare.com");
        sampleUser.setPasswordHash("$2a$10$encodedHash");
        sampleUser.setFullName("Dr. Rajesh Sharma");
        sampleUser.setAccountEnabled(true);
        sampleUser.setAccountLocked(false);
        sampleUser.setFailedLoginAttempts(0);
    }

    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void testAuthenticateSuccess() {
        LoginRequest req = new LoginRequest("dr_rajesh", "DoctorPass@123");
        when(userRepository.findByUsernameOrEmail("dr_rajesh", "dr_rajesh")).thenReturn(Optional.of(sampleUser));
        when(passwordService.matches("DoctorPass@123", "$2a$10$encodedHash")).thenReturn(true);
        when(userRepository.findRolesByUserId(1L)).thenReturn(sampleRoles);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyString(), anyString())).thenReturn("mock.jwt.token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        AuthResponse resp = authService.authenticate(req, "127.0.0.1", "JUnit-Agent");

        assertNotNull(resp);
        assertTrue(resp.isSuccess());
        assertEquals("mock.jwt.token", resp.getToken());
        assertEquals("dr_rajesh", resp.getUsername());
        verify(auditService).logEvent(eq("LOGIN_SUCCESS"), eq("dr_rajesh"), anyString(), anyString(), eq("SUCCESS"), anyString());
    }

    @Test
    @DisplayName("Should reject authentication when password does not match")
    void testAuthenticateInvalidPassword() {
        LoginRequest req = new LoginRequest("dr_rajesh", "WrongPassword");
        when(userRepository.findByUsernameOrEmail("dr_rajesh", "dr_rajesh")).thenReturn(Optional.of(sampleUser));
        when(passwordService.matches("WrongPassword", "$2a$10$encodedHash")).thenReturn(false);

        AuthResponse resp = authService.authenticate(req, "127.0.0.1", "JUnit-Agent");

        assertNotNull(resp);
        assertFalse(resp.isSuccess());
        verify(auditService).logEvent(eq("LOGIN_FAILURE"), eq("dr_rajesh"), anyString(), anyString(), eq("FAILURE"), anyString());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException on duplicate username during signup")
    void testSignupDuplicateUsername() {
        SignupRequest req = new SignupRequest();
        req.setUsername("dr_rajesh");
        req.setEmail("new@pulsesquare.com");
        when(userRepository.existsByUsername("dr_rajesh")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                authService.register(req, "127.0.0.1", "JUnit-Agent"));
    }

    @Test
    @DisplayName("Should successfully register a new healthcare staff member")
    void testSignupSuccess() {
        SignupRequest req = new SignupRequest();
        req.setUsername("nurse_priya");
        req.setEmail("nurse.priya@pulsesquare.com");
        req.setPassword("NursePass@123");
        req.setFullName("Priya Nair");
        req.setRole("NURSE");

        when(userRepository.existsByUsername("nurse_priya")).thenReturn(false);
        when(userRepository.existsByEmail("nurse.priya@pulsesquare.com")).thenReturn(false);
        when(passwordService.encode("NursePass@123")).thenReturn("$2a$10$nurseHashedPass");

        HmsRoles nurseRole = new HmsRoles();
        nurseRole.setId(2L);
        nurseRole.setName("ROLE_NURSE");
        nurseRole.setDescription("Nurse Role");

        when(roleRepository.findByName("ROLE_NURSE")).thenReturn(Optional.of(nurseRole));

        HmsUsers savedUser = new HmsUsers();
        savedUser.setId(2L);
        savedUser.setUsername("nurse_priya");
        savedUser.setEmail("nurse.priya@pulsesquare.com");
        savedUser.setFullName("Priya Nair");

        when(userRepository.save(any(HmsUsers.class), anySet())).thenReturn(savedUser);

        UserDto userDto = authService.register(req, "127.0.0.1", "JUnit-Agent");

        assertNotNull(userDto);
        assertEquals("nurse_priya", userDto.getUsername());
        assertEquals("NURSE", userDto.getRole());
        verify(auditService).logEvent(eq("SIGNUP_SUCCESS"), eq("nurse_priya"), anyString(), anyString(), eq("SUCCESS"), anyString());
    }
}
