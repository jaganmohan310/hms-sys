package com.pulsesquare.hms.service.service;

import com.pulsesquare.hms.model.dto.PatientCreateRequest;
import com.pulsesquare.hms.model.dto.PatientDto;

import java.util.List;
import java.util.Optional;

public interface PatientService {

    PatientDto registerPatient(PatientCreateRequest request, String ipAddress, String userAgent);

    Optional<PatientDto> getPatientByMrn(String mrn);

    Optional<PatientDto> getPatientById(Long id);

    List<PatientDto> searchPatients(String query, int page, int size);

    long countPatients(String query);

    PatientDto updatePatient(String mrn, PatientCreateRequest request, String ipAddress, String userAgent);

    boolean deletePatient(String mrn, String ipAddress, String userAgent);
}
