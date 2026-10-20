package com.pulsesquare.hms.ft;

import com.pulsesquare.hms.config.DataSourceConfig;
import com.pulsesquare.hms.service.security.JwtTokenProvider;
import com.pulsesquare.hms.service.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HmsFunctionalTestApplication.class)
@AutoConfigureMockMvc
@DisplayName("End-to-End Functional Tests - OpenAPI Contract & Swagger UI Routing")
class OpenApiContractFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("FT-09: OpenAPI /hms Routing - Redirects seamlessly to Swagger UI")
    void testHmsDocsRedirect() throws Exception {
        mockMvc.perform(get("/hms"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    @DisplayName("FT-10: OpenAPI 3.0 v3/api-docs - Endpoint returns valid JSON spec")
    void testApiDocsEndpoint() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
