package com.pulsesquare.hms.common;

import com.pulsesquare.hms.common.exception.DuplicateResourceException;
import com.pulsesquare.hms.common.exception.GlobalExceptionHandler;
import com.pulsesquare.hms.common.exception.InvalidCredentialsException;
import com.pulsesquare.hms.common.exception.ResourceNotFoundException;
import com.pulsesquare.hms.common.model.ApiError;
import com.pulsesquare.hms.common.model.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler & Common Model Tests")
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/test");
    }

    @Test
    @DisplayName("Should translate ResourceNotFoundException into 404 ApiError")
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "username", "john_doe");
        ResponseEntity<ApiError> response = exceptionHandler.handleApiException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("john_doe"));
        assertEquals("/api/v1/auth/test", response.getBody().getPath());
    }

    @Test
    @DisplayName("Should translate DuplicateResourceException into 409 ApiError")
    void testHandleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Username already in use");
        ResponseEntity<ApiError> response = exceptionHandler.handleApiException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("DUPLICATE_RESOURCE", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Should translate InvalidCredentialsException into 401 ApiError")
    void testHandleInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid password");
        ResponseEntity<ApiError> response = exceptionHandler.handleApiException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Should translate IllegalArgumentException into 400 ApiError")
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument provided");
        ResponseEntity<ApiError> response = exceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("BAD_REQUEST", response.getBody().getErrorCode());
        assertEquals("Invalid argument provided", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should translate unhandled Exception into 500 ApiError")
    void testHandleAllUncaughtException() {
        RuntimeException ex = new RuntimeException("Unexpected database timeout");
        ResponseEntity<ApiError> response = exceptionHandler.handleAllUncaughtException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Should construct standard ApiResponse envelopes")
    void testApiResponseEnvelope() {
        ApiResponse<String> okResponse = ApiResponse.ok("Registered successfully", "DATA_123");
        assertTrue(okResponse.isSuccess());
        assertEquals("Registered successfully", okResponse.getMessage());
        assertEquals("DATA_123", okResponse.getData());
        assertNotNull(okResponse.getTimestamp());

        ApiResponse<Integer> createdResponse = ApiResponse.created("Record created", 42);
        assertTrue(createdResponse.isSuccess());
        assertEquals(42, createdResponse.getData());

        ApiResponse<Void> failResponse = ApiResponse.failure("Action failed");
        assertFalse(failResponse.isSuccess());
        assertNull(failResponse.getData());
    }
}
