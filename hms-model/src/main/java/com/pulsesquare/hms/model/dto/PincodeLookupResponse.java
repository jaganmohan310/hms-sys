package com.pulsesquare.hms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Resolved geographic details for Indian postal PIN code")
public class PincodeLookupResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "6-digit PIN code", example = "503001")
    private String pincode;

    @Schema(description = "State / Union Territory", example = "TELANGANA")
    private String state;

    @Schema(description = "District", example = "NIZAMABAD")
    private String district;

    @Schema(description = "Primary Taluk / Tehsil / Division", example = "NIZAMABAD")
    private String taluk;

    @Schema(description = "Post offices and localities belonging to this PIN code")
    private List<String> offices = new ArrayList<>();

    public PincodeLookupResponse() {
    }

    public PincodeLookupResponse(String pincode, String state, String district, String taluk, List<String> offices) {
        this.pincode = pincode;
        this.state = state;
        this.district = district;
        this.taluk = taluk;
        this.offices = offices != null ? offices : new ArrayList<>();
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTaluk() {
        return taluk;
    }

    public void setTaluk(String taluk) {
        this.taluk = taluk;
    }

    public List<String> getOffices() {
        return offices;
    }

    public void setOffices(List<String> offices) {
        this.offices = offices;
    }
}
