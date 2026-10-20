package com.pulsesquare.hms.ft;

import com.pulsesquare.hms.config.DataSourceConfig;
import com.pulsesquare.hms.model.dto.UserDto;
import com.pulsesquare.hms.service.security.JwtTokenProvider;
import com.pulsesquare.hms.service.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = HmsFunctionalTestApplication.class)
@AutoConfigureMockMvc
@DisplayName("End-to-End Functional Tests - RBAC & Web Security")
class RbacSecurityFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("FT-06: Protected Endpoint - Unauthenticated request to /api/v1/auth/me returns 401")
    void testUnauthenticatedProfileAccess() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("FT-07: Protected Endpoint - Authenticated caller with valid JWT token retrieves profile")
    void testAuthenticatedProfileAccess() throws Exception {
        String token = "valid.jwt.bearer.token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn("nurse_anita");
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn("NURSE");

        UserDto nurseDto = new UserDto();
        nurseDto.setId(205L);
        nurseDto.setUsername("nurse_anita");
        nurseDto.setFullName("Anita Desai");
        nurseDto.setRole("NURSE");
        nurseDto.setDepartment("Pediatrics");

        when(authService.getUserProfile("nurse_anita")).thenReturn(nurseDto);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("nurse_anita"))
                .andExpect(jsonPath("$.data.role").value("NURSE"));
    }

    @Test
    @DisplayName("FT-08: Public Static Endpoints - Accessible without authentication")
    void testPublicStaticPagesAccessible() throws Exception {
        mockMvc.perform(get("/login.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk());
    }
}
