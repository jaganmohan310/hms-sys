package com.pulsesquare.hms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "Enterprise Authentication Response with JWT bearer token and user metadata")
public class AuthResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Indicates if authentication succeeded", example = "true")
    private boolean success;

    @Schema(description = "Human-readable status or error message", example = "Authentication successful. Welcome to PulseSquare HMS.")
    private String message;

    @Schema(description = "Stateless JWT Bearer Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token type schema", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Token validity lifetime in milliseconds", example = "86400000")
    private long expiresIn;

    @Schema(description = "Authenticated user profile details")
    private UserDto user;

    // Direct flat properties for legacy frontend compatibility
    @Schema(description = "Authenticated username", example = "admin")
    private String username;

    @Schema(description = "Authenticated full name", example = "Hospital Administrator")
    private String fullName;

    @Schema(description = "Primary assigned role", example = "ADMIN")
    private String role;

    @Schema(description = "Registered email address", example = "admin@pulsesquare.com")
    private String email;

    public AuthResponse() {
    }

    public static AuthResponse success(String message, String token, long expiresIn, UserDto user) {
        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(expiresIn);
        response.setUser(user);

        if (user != null) {
            response.setUsername(user.getUsername());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole());
            response.setEmail(user.getEmail());
        }
        return response;
    }

    public static AuthResponse failure(String message) {
        AuthResponse response = new AuthResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
