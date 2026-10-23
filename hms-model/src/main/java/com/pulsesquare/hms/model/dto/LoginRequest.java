package com.pulsesquare.hms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(description = "Enterprise Login Request payload for username/password authentication")
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Username or email is required")
    @Schema(description = "Registered username or corporate email", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "User credential password", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Maintain long-lived session", example = "true")
    private Boolean rememberMe = false;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
