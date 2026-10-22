package com.pulsesquare.hms.ws.controller;

import com.pulsesquare.hms.config.DataSourceConfig;
import com.pulsesquare.hms.oas.v1.api.AuthenticationAndAccessControlApi;
import com.pulsesquare.hms.oas.v1.model.*;
import com.pulsesquare.hms.service.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Enterprise Authentication & Access Control Controller.
 * Implements the OpenAPI 3.0 specification contract for user authentication and session management.
 */
@RestController
public class AuthController implements AuthenticationAndAccessControlApi {

    private final AuthService authService;
    private final DataSourceConfig dataSourceConfig;

    @Autowired
    public AuthController(AuthService authService, DataSourceConfig dataSourceConfig) {
        this.authService = authService;
        this.dataSourceConfig = dataSourceConfig;
    }

    // =========================================================================
    // 1. OAS Contract: POST /api/v1/auth/login (& fallback /api/auth/login)
    // =========================================================================
    @Override
    @PostMapping(value = {"/api/v1/auth/login", "/api/auth/login"})
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        HttpServletRequest httpRequest = getCurrentHttpRequest();
        String ipAddress = extractClientIp(httpRequest);
        String userAgent = (httpRequest != null && httpRequest.getHeader("User-Agent") != null) ? httpRequest.getHeader("User-Agent") : "";

        com.pulsesquare.hms.model.dto.LoginRequest serviceReq = new com.pulsesquare.hms.model.dto.LoginRequest();
        serviceReq.setUsername(loginRequest.getUsername());
        serviceReq.setPassword(loginRequest.getPassword());
        serviceReq.setRememberMe(Boolean.TRUE.equals(loginRequest.getRememberMe()));

        com.pulsesquare.hms.model.dto.AuthResponse serviceResp = authService.authenticate(serviceReq, ipAddress, userAgent);

        AuthResponse response = new AuthResponse();
        response.setSuccess(serviceResp.isSuccess());
        response.setMessage(serviceResp.getMessage());
        response.setToken(serviceResp.getToken());
        response.setTokenType(serviceResp.getTokenType());
        response.setExpiresIn(serviceResp.getExpiresIn());
        response.setUsername(serviceResp.getUsername());
        response.setFullName(serviceResp.getFullName());
        response.setRole(serviceResp.getRole());
        response.setEmail(serviceResp.getEmail());

        if (serviceResp.getUser() != null) {
            response.setUser(toOasUserDto(serviceResp.getUser()));
        }

        if (Boolean.TRUE.equals(serviceResp.isSuccess())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // =========================================================================
    // 2. OAS Contract: POST /api/v1/auth/signup (& fallback /api/auth/register, /api/auth/signup)
    // =========================================================================
    @Override
    @PostMapping(value = {"/api/v1/auth/signup", "/api/auth/register", "/api/auth/signup"})
    public ResponseEntity<ApiResponseUser> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        HttpServletRequest httpRequest = getCurrentHttpRequest();
        String ipAddress = extractClientIp(httpRequest);
        String userAgent = (httpRequest != null && httpRequest.getHeader("User-Agent") != null) ? httpRequest.getHeader("User-Agent") : "";

        com.pulsesquare.hms.model.dto.SignupRequest serviceReq = new com.pulsesquare.hms.model.dto.SignupRequest();
        serviceReq.setUsername(signupRequest.getUsername());
        serviceReq.setPassword(signupRequest.getPassword());
        serviceReq.setEmail(signupRequest.getEmail());
        serviceReq.setFullName(signupRequest.getFullName());
        serviceReq.setRole(signupRequest.getRole() != null ? signupRequest.getRole().getValue() : "PATIENT");
        serviceReq.setContactNumber(signupRequest.getContactNumber());
        serviceReq.setDepartment(signupRequest.getDepartment());

        com.pulsesquare.hms.model.dto.UserDto createdUser = authService.register(serviceReq, ipAddress, userAgent);

        ApiResponseUser response = new ApiResponseUser();
        response.setSuccess(true);
        response.setMessage("User account created successfully for '" + createdUser.getUsername() + "'. Please sign in.");
        response.setData(toOasUserDto(createdUser));
        response.setTimestamp(OffsetDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================================================================
    // 3. OAS Contract: GET /api/v1/auth/me (& fallback /api/auth/me)
    // =========================================================================
    @Override
    @GetMapping(value = {"/api/v1/auth/me", "/api/auth/me"})
    public ResponseEntity<ApiResponseUser> getCurrentUserProfile() {
        HttpServletRequest httpRequest = getCurrentHttpRequest();
        String authHeader = httpRequest != null ? httpRequest.getHeader("Authorization") : null;

        String username = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            username = authentication.getName();
        }

        com.pulsesquare.hms.model.dto.UserDto userDto;
        if (username != null) {
            userDto = authService.getUserProfile(username);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            userDto = authService.validateAndGetUser(authHeader);
        } else {
            throw new IllegalArgumentException("Missing or malformed Authorization header with Bearer token");
        }

        ApiResponseUser response = new ApiResponseUser();
        response.setSuccess(true);
        response.setMessage("User profile retrieved successfully");
        response.setData(toOasUserDto(userDto));
        response.setTimestamp(OffsetDateTime.now());

        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 4. OAS Contract: POST /api/v1/auth/logout (& fallback /api/auth/logout)
    // =========================================================================
    @Override
    @PostMapping(value = {"/api/v1/auth/logout", "/api/auth/logout"})
    public ResponseEntity<ApiResponseVoid> logoutUser() {
        HttpServletRequest httpRequest = getCurrentHttpRequest();
        String authHeader = httpRequest != null ? httpRequest.getHeader("Authorization") : null;
        String ipAddress = extractClientIp(httpRequest);
        String userAgent = (httpRequest != null && httpRequest.getHeader("User-Agent") != null) ? httpRequest.getHeader("User-Agent") : "";

        authService.logout(authHeader, ipAddress, userAgent);

        ApiResponseVoid response = new ApiResponseVoid();
        response.setSuccess(true);
        response.setMessage("Session successfully logged out");
        response.setTimestamp(OffsetDateTime.now());

        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 5. Diagnostics Status Probe (Hidden from Swagger UI - Infrastructure Endpoint)
    // =========================================================================
    @Hidden
    @GetMapping(value = {"/api/v1/auth/status", "/api/auth/status"})
    public ResponseEntity<ApiResponseStatus> getAuthStatus() {
        ApiResponseStatus response = new ApiResponseStatus();
        response.setSuccess(true);
        response.setMessage("Authentication service operational");
        response.setTimestamp(OffsetDateTime.now());

        ApiResponseStatusData statusData = new ApiResponseStatusData();
        statusData.setService("PulseSquare-Enterprise-HMS-WS");
        statusData.setVersion("1.0.0");
        statusData.setStatus("UP");
        statusData.setDbMode("IBM_DB2_ENTERPRISE (" + dataSourceConfig.getDb2Host() + ":" + dataSourceConfig.getDb2Port() + "/" + dataSourceConfig.getDb2Database() + ")");

        response.setData(statusData);
        return ResponseEntity.ok(response);
    }

    // Helper: Map model.UserDto to OAS UserDto
    private UserDto toOasUserDto(com.pulsesquare.hms.model.dto.UserDto source) {
        if (source == null) return null;
        UserDto target = new UserDto();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setEmail(source.getEmail());
        target.setFullName(source.getFullName());
        target.setDepartment(source.getDepartment());
        target.setContactNumber(source.getContactNumber());
        target.setAccountEnabled(source.isAccountEnabled());
        target.setRole(source.getRole());
        if (source.getRoles() != null) {
            target.setRoles(new java.util.ArrayList<>(source.getRoles()));
        }
        if (source.getCreatedAt() != null) {
            target.setCreatedAt(source.getCreatedAt().atOffset(java.time.ZoneOffset.UTC));
        }
        return target;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
