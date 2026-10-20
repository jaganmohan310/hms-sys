package com.pulsesquare.hms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Enterprise User Profile DTO")
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique database ID", example = "1")
    private Long id;

    @Schema(description = "Username handle", example = "admin")
    private String username;

    @Schema(description = "Corporate or personal email", example = "admin@pulsesquare.com")
    private String email;

    @Schema(description = "Full user name", example = "Hospital Administrator")
    private String fullName;

    @Schema(description = "Primary user role", example = "ADMIN")
    private String role;

    @Schema(description = "Assigned roles", example = "[\"ROLE_ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "Department name", example = "Administration")
    private String department;

    @Schema(description = "Contact phone number", example = "+1-800-555-0199")
    private String contactNumber;

    @Schema(description = "Account enabled status", example = "true")
    private boolean accountEnabled;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    public UserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isAccountEnabled() {
        return accountEnabled;
    }

    public void setAccountEnabled(boolean accountEnabled) {
        this.accountEnabled = accountEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
