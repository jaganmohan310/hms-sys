package com.pulsesquare.hms.oas.v1;

import com.pulsesquare.hms.oas.v1.model.LoginRequest;
import com.pulsesquare.hms.oas.v1.model.SignupRequest;
import com.pulsesquare.hms.oas.v1.model.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OpenAPI 3.0 Contract & Stubs Verification Tests")
class OasContractTest {

    @Test
    @DisplayName("Should verify HMS Functional OpenAPI specifications exist on classpath for v1 and v2")
    void testSpecResourceExists() {
        InputStream isV1 = getClass().getResourceAsStream("/openapi/v1/hms-api.yaml");
        assertNotNull(isV1, "OpenAPI v1 specification YAML file must be present in classpath");

        InputStream isV1Legacy = getClass().getResourceAsStream("/openapi/v1/hms-auth-api.yaml");
        assertNotNull(isV1Legacy, "OpenAPI v1 auth specification YAML file must be present in classpath");

        InputStream isV2 = getClass().getResourceAsStream("/openapi/v2/hms-api.yaml");
        assertNotNull(isV2, "OpenAPI v2 specification YAML file must be present in classpath");
    }

    @Test
    @DisplayName("Should verify generated OAS Model stubs instantiate and set properties correctly")
    void testGeneratedModels() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testadmin");
        loginRequest.setPassword("Secret123");
        loginRequest.setRememberMe(true);

        assertEquals("testadmin", loginRequest.getUsername());
        assertEquals("Secret123", loginRequest.getPassword());
        assertTrue(loginRequest.getRememberMe());

        SignupRequest signup = new SignupRequest();
        signup.setUsername("doctor1");
        signup.setEmail("doc@pulsesquare.com");
        signup.setRole(SignupRequest.RoleEnum.DOCTOR);
        assertEquals("doctor1", signup.getUsername());
        assertEquals(SignupRequest.RoleEnum.DOCTOR, signup.getRole());

        UserDto user = new UserDto();
        user.setId(101L);
        user.setUsername("doctor1");
        user.setRole("DOCTOR");
        assertEquals(101L, user.getId());
    }
}
