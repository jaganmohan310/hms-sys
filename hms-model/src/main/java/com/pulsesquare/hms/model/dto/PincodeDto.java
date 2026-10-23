package com.pulsesquare.hms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(description = "Official Indian Postal Pincode & Office Entry")
public class PincodeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "6-digit Indian Postal PIN code", example = "503001")
    private String pincode;

    @Schema(description = "Post office name / Locality", example = "Nizamabad H.O")
    private String officeName;

    @Schema(description = "Office type (H.O, S.O, B.O)", example = "H.O")
    private String officeType;

    @Schema(description = "Delivery status", example = "Delivery")
    private String deliveryStatus;

    @Schema(description = "Postal Division / Taluk", example = "Nizamabad Division")
    private String divisionName;

    @Schema(description = "Postal Region", example = "Hyderabad Region")
    private String regionName;

    @Schema(description = "Postal Circle", example = "Telangana Circle")
    private String circleName;

    @Schema(description = "District name", example = "NIZAMABAD")
    private String district;

    @Schema(description = "State / Union Territory", example = "TELANGANA")
    private String stateName;

    @Schema(description = "Latitude coordinate", example = "18.6725")
    private String latitude;

    @Schema(description = "Longitude coordinate", example = "78.0941")
    private String longitude;

    public PincodeDto() {
    }

    public PincodeDto(String pincode, String officeName, String officeType, String deliveryStatus,
                      String divisionName, String regionName, String circleName,
                      String district, String stateName, String latitude, String longitude) {
        this.pincode = pincode;
        this.officeName = officeName;
        this.officeType = officeType;
        this.deliveryStatus = deliveryStatus;
        this.divisionName = divisionName;
        this.regionName = regionName;
        this.circleName = circleName;
        this.district = district;
        this.stateName = stateName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getOfficeType() {
        return officeType;
    }

    public void setOfficeType(String officeType) {
        this.officeType = officeType;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
