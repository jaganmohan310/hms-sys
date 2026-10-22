package com.pulsesquare.hms.service.impl;

import com.pulsesquare.hms.common.exception.BadRequestException;
import com.pulsesquare.hms.common.exception.ResourceNotFoundException;
import com.pulsesquare.hms.jooq.tables.pojos.HmsPatients;
import com.pulsesquare.hms.model.dto.PatientCreateRequest;
import com.pulsesquare.hms.model.dto.PatientDto;
import com.pulsesquare.hms.repo.repository.PatientRepository;
import com.pulsesquare.hms.service.service.AuditService;
import com.pulsesquare.hms.service.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    private final PatientRepository patientRepository;
    private final AuditService auditService;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, AuditService auditService) {
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    @Override
    public PatientDto registerPatient(PatientCreateRequest request, String ipAddress, String userAgent) {
        if (request == null) {
            throw new BadRequestException("Patient registration details cannot be null");
        }

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("First name is required");
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new BadRequestException("Last name is required");
        }

        if (request.getGender() == null || request.getGender().trim().isEmpty()) {
            throw new BadRequestException("Gender is required");
        }

        // Generate persistent unique MRN
        String mrn = patientRepository.generateNextMrn();

        HmsPatients patient = new HmsPatients();
        patient.setMrn(mrn);
        patient.setTitle(request.getTitle() != null && !request.getTitle().isBlank() ? request.getTitle() : "Mr");
        patient.setFirstName(request.getFirstName().trim());
        patient.setMiddleName(request.getMiddleName() != null ? request.getMiddleName().trim() : null);
        patient.setLastName(request.getLastName().trim());
        patient.setGender(request.getGender().trim().toUpperCase());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setAgeYears(request.getAgeYears());
        patient.setAgeMonths(request.getAgeMonths());
        patient.setAgeDays(request.getAgeDays());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setContactNumber(request.getContactNumber() != null ? request.getContactNumber().trim() : null);
        patient.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        patient.setMaritalStatus(request.getMaritalStatus());
        patient.setReligion(request.getReligion());
        patient.setAddress(request.getAddress());
        patient.setCity(request.getCity());
        patient.setState(request.getState());
        patient.setCountry(request.getCountry() != null ? request.getCountry() : "India");
        patient.setZipCode(request.getZipCode());
        patient.setReferralDoctor(request.getReferralDoctor());
        patient.setDepartment(request.getDepartment() != null ? request.getDepartment() : "Out-Patient");
        patient.setRegistrationFee(request.getRegistrationFee() != null ? request.getRegistrationFee() : BigDecimal.ZERO);
        patient.setPaymentType(request.getPaymentType() != null ? request.getPaymentType() : "Cash");
        patient.setActive(true);
        patient.setRegisteredAt(LocalDateTime.now());

        HmsPatients saved = patientRepository.save(patient);

        auditService.logEvent("PATIENT_REGISTERED", mrn, ipAddress, userAgent, "SUCCESS",
                "New patient registered: " + saved.getFirstName() + " " + saved.getLastName() + " (MRN: " + mrn + ")");

        logger.info("Patient registered successfully: MRN={}, Name={} {}, Dept={}",
                mrn, saved.getFirstName(), saved.getLastName(), saved.getDepartment());

        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientDto> getPatientByMrn(String mrn) {
        if (mrn == null || mrn.trim().isEmpty()) {
            return Optional.empty();
        }
        return patientRepository.findByMrn(mrn.trim()).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientDto> getPatientById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return patientRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> searchPatients(String query, int page, int size) {
        return patientRepository.search(query, page, size)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countPatients(String query) {
        return patientRepository.countSearch(query);
    }

    private PatientDto toDto(HmsPatients entity) {
        if (entity == null) return null;

        PatientDto dto = new PatientDto();
        dto.setId(entity.getId());
        dto.setMrn(entity.getMrn());
        dto.setTitle(entity.getTitle());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setGender(entity.getGender());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setAgeYears(entity.getAgeYears());
        dto.setAgeMonths(entity.getAgeMonths());
        dto.setAgeDays(entity.getAgeDays());
        dto.setBloodGroup(entity.getBloodGroup());
        dto.setContactNumber(entity.getContactNumber());
        dto.setEmail(entity.getEmail());
        dto.setMaritalStatus(entity.getMaritalStatus());
        dto.setReligion(entity.getReligion());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setZipCode(entity.getZipCode());
        dto.setReferralDoctor(entity.getReferralDoctor());
        dto.setDepartment(entity.getDepartment());
        dto.setRegistrationFee(entity.getRegistrationFee());
        dto.setPaymentType(entity.getPaymentType());
        dto.setActive(Boolean.TRUE.equals(entity.getActive()));
        dto.setRegisteredAt(entity.getRegisteredAt());

        return dto;
    }
}
