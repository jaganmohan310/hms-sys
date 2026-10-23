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

    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    @Schema(description = "Registration Date", example = "2026-10-22")
    private LocalDate regDate;

    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    @Schema(description = "Registration Expiry Date", example = "2027-10-22")
    private LocalDate regExpiryDate;

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
    @Schema(description = "Date of Birth (Column 1 - Standard SQL Date)", example = "1988-06-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Date of Birth (Column 2 - Text string as entered in form)", example = "15/06/1988")
    private String dobRaw;

    @Schema(description = "Age in years", example = "38")
    private Integer ageYears;

    @Schema(description = "Age in months", example = "4")
    private Integer ageMonths;

    @Schema(description = "Age in days", example = "12")
    private Integer ageDays;

    @Schema(description = "Blood group", example = "O+")
    private String bloodGroup;

    @Schema(description = "Contact phone number (Mobile)", example = "+91-9876543210")
    private String contactNumber;

    @Schema(description = "Landline phone number", example = "08462-234567")
    private String landline;

    @Schema(description = "Patient email", example = "rahul.sharma@example.com")
    private String email;

    @Schema(description = "Marital status", example = "Married")
    private String maritalStatus;

    @Schema(description = "Religion", example = "Hindu")
    private String religion;

    @Schema(description = "Street / Residential address", example = "A-42, Silicon Enclave")
    private String address;

    @Schema(description = "City / District", example = "NIZAMABAD")
    private String city;

    @Schema(description = "State", example = "TELANGANA")
    private String state;

    @Schema(description = "Country", example = "INDIA")
    private String country = "INDIA";

    @Schema(description = "Zip / Postal code", example = "503001")
    private String zipCode;

    @Schema(description = "Area / Taluk / Mandal", example = "NIZAMABAD NORTH")
    private String area;

    @Schema(description = "Education Level", example = "Graduate")
    private String educationLevel;

    @Schema(description = "Occupation", example = "Private Service")
    private String occupation;

    @Schema(description = "Identification Birth Mark", example = "Mole on right wrist")
    private String birthMark;

    @Schema(description = "Comments / Clinical Remarks")
    private String comments;

    @Schema(description = "Photograph file name", example = "patient_photo.jpg")
    private String photoFileName;

    @Schema(description = "Photograph base64 / data URL")
    private String photoData;

    @Schema(description = "Referral Doctor", example = "Dr. Rajesh Sharma")
    private String referralDoctor;

    @Schema(description = "Department", example = "Out-Patient")
    private String department = "Out-Patient";

    @Schema(description = "Registration Fee", example = "100.00")
    private BigDecimal registrationFee = BigDecimal.ZERO;

    @Schema(description = "Discount Type", example = "No Discount")
    private String discountType = "No Discount";

    @Schema(description = "Discount Percentage", example = "0.00")
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Schema(description = "Discount Amount", example = "0.00")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Schema(description = "Net Amount Payable", example = "100.00")
    private BigDecimal netAmount = BigDecimal.ZERO;

    @Schema(description = "Paid Amount", example = "100.00")
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Schema(description = "Due Amount", example = "0.00")
    private BigDecimal dueAmount = BigDecimal.ZERO;

    @Schema(description = "Payment Type", example = "Cash")
    private String paymentType = "Cash";

    public PatientCreateRequest() {
    }

    public LocalDate getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDate regDate) {
        this.regDate = regDate;
    }

    public LocalDate getRegExpiryDate() {
        return regExpiryDate;
    }

    public void setRegExpiryDate(LocalDate regExpiryDate) {
        this.regExpiryDate = regExpiryDate;
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

    public String getDobRaw() {
        return dobRaw;
    }

    public void setDobRaw(String dobRaw) {
        this.dobRaw = dobRaw;
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

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getBirthMark() {
        return birthMark;
    }

    public void setBirthMark(String birthMark) {
        this.birthMark = birthMark;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
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

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
