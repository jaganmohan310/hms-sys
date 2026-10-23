package com.pulsesquare.hms.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenApiConfigTest {

    @Test
    void testCustomOpenAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("PulseSquare Healthcare Management System (HMS) REST API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertFalse(openAPI.getServers().isEmpty());
    }
}
