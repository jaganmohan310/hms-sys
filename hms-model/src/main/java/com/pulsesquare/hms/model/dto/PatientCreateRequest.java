package com.pulsesquare.hms.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pulsesquare.hms.common.util.FlexibleLocalDateDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Demographic Payload for Registering a New Patient (OPD, Lab, or IP)")
public class PatientCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Salutation / Title", example = "Mr")
    private String title = "Mr";

    @NotBlank(message = "First name is mandatory")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Schema(description = "First name", example = "Rahul", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    @Schema(description = "Middle name", example = "Kumar")
    private String middleName;

    @NotBlank(message = "Last name is mandatory")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Schema(description = "Last name", example = "Sharma", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotBlank(message = "Gender is mandatory")
    @Schema(description = "Gender (MALE, FEMALE, OTHER)", example = "MALE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gender;

    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    @Schema(description = "Date of Birth", example = "1988-06-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Age in years", example = "38")
    private Integer ageYears;

    @Schema(description = "Age in months", example = "4")
    private Integer ageMonths;

    @Schema(description = "Age in days", example = "12")
    private Integer ageDays;

    @Schema(description = "Blood group", example = "O+")
    private String bloodGroup;

    @Schema(description = "Contact phone number", example = "+91-9876543210")
    private String contactNumber;

    @Schema(description = "Patient email", example = "rahul.sharma@example.com")
    private String email;

    @Schema(description = "Marital status", example = "Married")
    private String maritalStatus;

    @Schema(description = "Religion", example = "Hindu")
    private String religion;

    @Schema(description = "Street / Residential address", example = "A-42, Silicon Enclave")
    private String address;

    @Schema(description = "City", example = "Nizamabad")
    private String city;

    @Schema(description = "State", example = "Telangana")
    private String state;

    @Schema(description = "Country", example = "India")
    private String country = "India";

    @Schema(description = "Zip / Postal code", example = "503001")
    private String zipCode;

    @Schema(description = "Referral Doctor", example = "Dr. Rajesh Sharma")
    private String referralDoctor;

    @Schema(description = "Department", example = "Out-Patient")
    private String department = "Out-Patient";

    @Schema(description = "Registration Fee", example = "100.00")
    private BigDecimal registrationFee = BigDecimal.ZERO;

    @Schema(description = "Payment Type", example = "Cash")
    private String paymentType = "Cash";

    public PatientCreateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(Integer ageYears) {
        this.ageYears = ageYears;
    }

    public Integer getAgeMonths() {
        return ageMonths;
    }

    public void setAgeMonths(Integer ageMonths) {
        this.ageMonths = ageMonths;
    }

    public Integer getAgeDays() {
        return ageDays;
    }

    public void setAgeDays(Integer ageDays) {
        this.ageDays = ageDays;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getReferralDoctor() {
        return referralDoctor;
    }

    public void setReferralDoctor(String referralDoctor) {
        this.referralDoctor = referralDoctor;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(BigDecimal registrationFee) {
        this.registrationFee = registrationFee;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
