package com.pulsesquare.hms.common.exception;

import com.pulsesquare.hms.common.model.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enterprise Centralized REST Exception Handler.
 * Intercepts domain and validation exceptions, translating them into RFC 7807 problem details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex, HttpServletRequest request) {
        logger.warn("Domain exception [{}]: {} at {}", ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        ApiError error = new ApiError(
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                ex.getErrorCode(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        logger.warn("Bad request: {} at {}", ex.getMessage(), request.getRequestURI());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                "BAD_REQUEST",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        logger.warn("Constraint violation: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for one or more fields",
                "VALIDATION_ERROR",
                request.getRequestURI()
        );
        error.setFieldErrors(fieldErrors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        String path = "";
        if (request instanceof ServletWebRequest) {
            path = ((ServletWebRequest) request).getRequest().getRequestURI();
        }

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for request payload",
                "VALIDATION_FAILED",
                path
        );
        error.setFieldErrors(fieldErrors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled enterprise exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected internal error occurred. Please contact hospital support.",
                "INTERNAL_SERVER_ERROR",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
