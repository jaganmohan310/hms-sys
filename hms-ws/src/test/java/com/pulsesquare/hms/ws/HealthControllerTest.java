package com.pulsesquare.hms.ws;

import com.pulsesquare.hms.config.DataSourceConfig;
import com.pulsesquare.hms.service.security.JwtTokenProvider;
import com.pulsesquare.hms.ws.controller.HealthController;
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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HealthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("HealthController Operational Health Endpoint Tests")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSourceConfig dataSourceConfig;

    @MockBean
    private DataSource dataSource;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("GET /health should return 200 OK with UP status and component telemetry")
    void testGetHealthSuccess() throws Exception {
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);

        when(dataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.execute(anyString())).thenReturn(true);

        when(dataSourceConfig.getDb2Host()).thenReturn("localhost");
        when(dataSourceConfig.getDb2Port()).thenReturn(50000);
        when(dataSourceConfig.getDb2Database()).thenReturn("hms");

        mockMvc.perform(get("/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("PulseSquare-Enterprise-HMS"))
                .andExpect(jsonPath("$.components.database.status").value("UP"))
                .andExpect(jsonPath("$.components.database.engine").value("IBM Db2 Enterprise"))
                .andExpect(jsonPath("$.components.runtime.status").value("UP"));
    }

    @Test
    @DisplayName("GET /api/v1/health should return 200 OK for API gateway probes")
    void testGetApiHealthSuccess() throws Exception {
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);

        when(dataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.execute(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/v1/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
