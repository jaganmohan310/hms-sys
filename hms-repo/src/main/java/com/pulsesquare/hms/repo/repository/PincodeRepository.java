package com.pulsesquare.hms.repo.repository;

import com.pulsesquare.hms.model.dto.PincodeDto;
import com.pulsesquare.hms.model.dto.PincodeLookupResponse;

import java.util.List;
import java.util.Optional;

public interface PincodeRepository {

    Optional<PincodeLookupResponse> findByPincode(String pincode);

    List<String> findDistinctStates();

    List<String> findDistrictsByState(String state);

    List<String> findOfficesByDistrict(String state, String district);

    List<PincodeDto> search(String query, int limit);

    long count();

    void batchInsert(List<PincodeDto> batch);
}
