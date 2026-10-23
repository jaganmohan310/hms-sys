package com.pulsesquare.hms.ft;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulsesquare.hms.config.DataSourceConfig;
import com.pulsesquare.hms.model.dto.AuthResponse;
import com.pulsesquare.hms.model.dto.UserDto;
import com.pulsesquare.hms.oas.v1.model.LoginRequest;
import com.pulsesquare.hms.oas.v1.model.SignupRequest;
import com.pulsesquare.hms.service.security.JwtTokenProvider;
import com.pulsesquare.hms.service.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = HmsFunctionalTestApplication.class)
@AutoConfigureMockMvc
@DisplayName("End-to-End Functional Tests - Healthcare Authentication & Operations")
class AuthFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("FT-01: User Registration Workflow - Sign up new staff member through REST API")
    void testEndToEndStaffRegistration() throws Exception {
        SignupRequest signup = new SignupRequest();
        signup.setUsername("dr_arjun");
        signup.setPassword("DoctorSecure@2026");
        signup.setEmail("arjun@pulsesquare.com");
        signup.setFullName("Dr. Arjun Verma");
        signup.setRole(SignupRequest.RoleEnum.DOCTOR);
        signup.setDepartment("Neurology");
        signup.setContactNumber("+91-9811122233");

        UserDto createdUser = new UserDto();
        createdUser.setId(101L);
        createdUser.setUsername("dr_arjun");
        createdUser.setEmail("arjun@pulsesquare.com");
        createdUser.setFullName("Dr. Arjun Verma");
        createdUser.setRole("DOCTOR");
        createdUser.setDepartment("Neurology");
        createdUser.setContactNumber("+91-9811122233");
        createdUser.setAccountEnabled(true);

        when(authService.register(any(), any(), any())).thenReturn(createdUser);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("dr_arjun"))
                .andExpect(jsonPath("$.data.role").value("DOCTOR"))
                .andExpect(jsonPath("$.data.department").value("Neurology"));
    }

    @Test
    @DisplayName("FT-02: User Login & JWT Token Issuance Workflow")
    void testEndToEndStaffLogin() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("dr_arjun");
        login.setPassword("DoctorSecure@2026");
        login.setRememberMe(true);

        UserDto user = new UserDto();
        user.setId(101L);
        user.setUsername("dr_arjun");
        user.setRole("DOCTOR");
        user.setFullName("Dr. Arjun Verma");

        AuthResponse authResponse = AuthResponse.success(
                "Authentication successful. Welcome to PulseSquare HMS.",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.signature",
                86400000L,
                user
        );

        when(authService.authenticate(any(), any(), any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.signature"))
                .andExpect(jsonPath("$.role").value("DOCTOR"))
                .andExpect(jsonPath("$.username").value("dr_arjun"));
    }

    @Test
    @DisplayName("FT-03: Invalid Credentials Login - Returns 401 with appropriate message")
    void testInvalidCredentialsLogin() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("dr_arjun");
        login.setPassword("WrongPassword");

        AuthResponse failureResponse = AuthResponse.failure("Invalid username or password");
        when(authService.authenticate(any(), any(), any())).thenReturn(failureResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("FT-04: System Diagnostics - Health check returns 200 with runtime info")
    void testSystemDiagnosticsEndpoint() throws Exception {
        when(dataSourceConfig.getDb2Host()).thenReturn("localhost");
        when(dataSourceConfig.getDb2Port()).thenReturn(50000);
        when(dataSourceConfig.getDb2Database()).thenReturn("hms");

        mockMvc.perform(get("/api/v1/auth/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("PulseSquare-Enterprise-HMS-WS"));
    }

    @Test
    @DisplayName("FT-05: User Logout Workflow - Invalidate active session")
    void testUserLogoutWorkflow() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer valid.token.sample"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Session successfully logged out"));
    }
}
