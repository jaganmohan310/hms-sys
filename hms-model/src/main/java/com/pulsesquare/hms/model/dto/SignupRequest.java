package com.pulsesquare.hms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Schema(description = "Enterprise Signup Request payload for staff or patient onboarding")
public class SignupRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain alphanumeric characters, dots, underscores, and hyphens")
    @Schema(description = "Unique user identifier", example = "dr_rajesh", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long")
    @Schema(description = "Secure password", example = "HealthPass@2026", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "Corporate or personal email address", example = "rajesh.r@pulsesquare.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Schema(description = "Full legal name or clinician title", example = "Dr. Rajesh Raman", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Schema(description = "Healthcare role assignment: ADMIN, DOCTOR, NURSE, RECEPTIONIST, PHARMACIST, LAB_TECH, PATIENT", example = "DOCTOR", defaultValue = "PATIENT")
    private String role = "PATIENT";

    @Schema(description = "Contact phone number", example = "+91-9876543210")
    private String contactNumber;

    @Schema(description = "Hospital department or clinical wing", example = "Cardiology & Critical Care")
    private String department;

    public SignupRequest() {
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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
