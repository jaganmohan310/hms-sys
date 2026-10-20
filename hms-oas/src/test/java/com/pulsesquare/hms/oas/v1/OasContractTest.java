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
    @DisplayName("Should verify OpenAPI v1 spec resource exists on classpath")
    void testSpecResourceExists() {
        InputStream is = getClass().getResourceAsStream("/openapi/v1/hms-auth-api.yaml");
        assertNotNull(is, "OpenAPI v1 specification YAML file must be present in classpath");
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
