package com.pulsesquare.hms.common;

import com.pulsesquare.hms.common.exception.DuplicateResourceException;
import com.pulsesquare.hms.common.exception.GlobalExceptionHandler;
import com.pulsesquare.hms.common.exception.InvalidCredentialsException;
import com.pulsesquare.hms.common.exception.ResourceNotFoundException;
import com.pulsesquare.hms.common.model.ApiError;
import com.pulsesquare.hms.common.model.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

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
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "username", "john_doe");
        ResponseEntity<ApiError> response = exceptionHandler.handleApiException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("john_doe"));
    }

    @Test
    void testHandleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Username already in use");
        ResponseEntity<ApiError> response = exceptionHandler.handleApiException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("DUPLICATE_RESOURCE", response.getBody().getErrorCode());
    }

    @Test
    void testHandleInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid password");
        ResponseEntity<ApiError> response = exceptionHandler.handleApiException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getErrorCode());
    }

    @Test
    void testApiResponseEnvelope() {
        ApiResponse<String> successResponse = ApiResponse.ok("Registered successfully", "DATA_123");
        assertTrue(successResponse.isSuccess());
        assertEquals("Registered successfully", successResponse.getMessage());
        assertEquals("DATA_123", successResponse.getData());
        assertNotNull(successResponse.getTimestamp());
    }
}
