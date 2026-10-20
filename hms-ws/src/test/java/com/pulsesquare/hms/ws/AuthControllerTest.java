package com.pulsesquare.hms.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulsesquare.hms.config.DataSourceConfig;
import com.pulsesquare.hms.model.dto.AuthResponse;
import com.pulsesquare.hms.model.dto.UserDto;
import com.pulsesquare.hms.oas.v1.model.LoginRequest;
import com.pulsesquare.hms.oas.v1.model.SignupRequest;
import com.pulsesquare.hms.service.security.JwtTokenProvider;
import com.pulsesquare.hms.service.service.AuthService;
import com.pulsesquare.hms.ws.controller.AuthController;
import com.pulsesquare.hms.ws.security.JwtAuthenticationFilter;
import com.pulsesquare.hms.ws.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("AuthController REST Endpoint Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private DataSourceConfig dataSourceConfig;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /api/v1/auth/login should authenticate and return 200 with JWT")
    void testLoginSuccess() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");
        userDto.setRole("ADMIN");

        AuthResponse authResp = AuthResponse.success("Login successful", "jwt.token.value", 86400000L, userDto);
        when(authService.authenticate(any(), any(), any())).thenReturn(authResp);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt.token.value"))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup should create user and return 201 Created")
    void testSignupSuccess() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setUsername("dr_sharma");
        req.setPassword("SharmaPass@123");
        req.setEmail("sharma@pulsesquare.com");
        req.setFullName("Dr. Sharma");
        req.setRole(SignupRequest.RoleEnum.DOCTOR);

        UserDto createdDto = new UserDto();
        createdDto.setId(5L);
        createdDto.setUsername("dr_sharma");
        createdDto.setRole("DOCTOR");
        when(authService.register(any(), any(), any())).thenReturn(createdDto);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("dr_sharma"));
    }

    @Test
    @DisplayName("GET /api/v1/auth/status should return 200 with DB2 dialect diagnostics")
    void testStatusSuccess() throws Exception {
        when(dataSourceConfig.getDb2Host()).thenReturn("localhost");
        when(dataSourceConfig.getDb2Port()).thenReturn(50000);
        when(dataSourceConfig.getDb2Database()).thenReturn("hms");

        mockMvc.perform(get("/api/v1/auth/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("PulseSquare-Enterprise-HMS-WS"));
    }
}
