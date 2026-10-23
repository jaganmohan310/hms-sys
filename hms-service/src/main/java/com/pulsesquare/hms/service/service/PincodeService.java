package com.pulsesquare.hms.service.service;

import com.pulsesquare.hms.model.dto.PincodeDto;
import com.pulsesquare.hms.model.dto.PincodeLookupResponse;

import java.util.List;
import java.util.Optional;

public interface PincodeService {

    Optional<PincodeLookupResponse> lookup(String pincode);

    List<String> getStates();

    List<String> getDistricts(String state);

    List<String> getOffices(String state, String district);

    List<PincodeDto> search(String query, int limit);

    int loadPincodesFromCsvIfEmpty();
}
