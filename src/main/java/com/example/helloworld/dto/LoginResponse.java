package com.example.helloworld.dto;

public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private String username;
    private String fullName;
    private String role;
    private String email;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message);
    }

    public static LoginResponse success(String message, String token, String username, String fullName, String role, String email) {
        LoginResponse response = new LoginResponse(true, message);
        response.token = token;
        response.username = username;
        response.fullName = fullName;
        response.role = role;
        response.email = email;
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
