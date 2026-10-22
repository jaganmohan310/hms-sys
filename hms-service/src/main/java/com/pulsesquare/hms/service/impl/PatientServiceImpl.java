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
import java.time.LocalDate;
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
        patient.setRegDate(request.getRegDate() != null ? request.getRegDate() : LocalDate.now());
        patient.setRegExpiryDate(request.getRegExpiryDate() != null ? request.getRegExpiryDate() : LocalDate.now().plusYears(1));
        patient.setTitle(request.getTitle() != null && !request.getTitle().isBlank() ? request.getTitle() : "Mr");
        patient.setFirstName(request.getFirstName().trim());
        patient.setMiddleName(request.getMiddleName() != null ? request.getMiddleName().trim() : null);
        patient.setLastName(request.getLastName().trim());
        patient.setReligion(request.getReligion());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setDobRaw(request.getDobRaw());
        patient.setAgeYears(request.getAgeYears());
        patient.setAgeMonths(request.getAgeMonths());
        patient.setAgeDays(request.getAgeDays());
        patient.setGender(request.getGender().trim().toUpperCase());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setContactNumber(request.getContactNumber() != null ? request.getContactNumber().trim() : null);
        patient.setLandline(request.getLandline() != null ? request.getLandline().trim() : null);
        patient.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        patient.setMaritalStatus(request.getMaritalStatus());
        patient.setAddress(request.getAddress());
        patient.setZipCode(request.getZipCode());
        patient.setCountry(request.getCountry() != null ? request.getCountry() : "INDIA");
        patient.setState(request.getState());
        patient.setCity(request.getCity());
        patient.setArea(request.getArea());
        patient.setEducationLevel(request.getEducationLevel());
        patient.setOccupation(request.getOccupation());
        patient.setBirthMark(request.getBirthMark());
        patient.setPhotoFileName(request.getPhotoFileName());
        patient.setPhotoData(request.getPhotoData());
        patient.setComments(request.getComments());
        patient.setRegistrationFee(request.getRegistrationFee() != null ? request.getRegistrationFee() : BigDecimal.ZERO);
        patient.setDiscountType(request.getDiscountType() != null ? request.getDiscountType() : "No Discount");
        patient.setDiscountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : BigDecimal.ZERO);
        patient.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        patient.setNetAmount(request.getNetAmount() != null ? request.getNetAmount() : BigDecimal.ZERO);
        patient.setPaidAmount(request.getPaidAmount() != null ? request.getPaidAmount() : BigDecimal.ZERO);
        patient.setDueAmount(request.getDueAmount() != null ? request.getDueAmount() : BigDecimal.ZERO);
        patient.setPaymentType(request.getPaymentType() != null ? request.getPaymentType() : "Cash");
        patient.setReferralDoctor(request.getReferralDoctor());
        patient.setDepartment(request.getDepartment() != null ? request.getDepartment() : "Out-Patient");
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
        dto.setRegDate(entity.getRegDate());
        dto.setRegExpiryDate(entity.getRegExpiryDate());
        dto.setTitle(entity.getTitle());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setReligion(entity.getReligion());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setDobRaw(entity.getDobRaw());
        dto.setAgeYears(entity.getAgeYears());
        dto.setAgeMonths(entity.getAgeMonths());
        dto.setAgeDays(entity.getAgeDays());
        dto.setGender(entity.getGender());
        dto.setBloodGroup(entity.getBloodGroup());
        dto.setContactNumber(entity.getContactNumber());
        dto.setLandline(entity.getLandline());
        dto.setEmail(entity.getEmail());
        dto.setMaritalStatus(entity.getMaritalStatus());
        dto.setAddress(entity.getAddress());
        dto.setZipCode(entity.getZipCode());
        dto.setCountry(entity.getCountry());
        dto.setState(entity.getState());
        dto.setCity(entity.getCity());
        dto.setArea(entity.getArea());
        dto.setEducationLevel(entity.getEducationLevel());
        dto.setOccupation(entity.getOccupation());
        dto.setBirthMark(entity.getBirthMark());
        dto.setPhotoFileName(entity.getPhotoFileName());
        dto.setPhotoData(entity.getPhotoData());
        dto.setComments(entity.getComments());
        dto.setRegistrationFee(entity.getRegistrationFee());
        dto.setDiscountType(entity.getDiscountType());
        dto.setDiscountPercent(entity.getDiscountPercent());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setNetAmount(entity.getNetAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setDueAmount(entity.getDueAmount());
        dto.setPaymentType(entity.getPaymentType());
        dto.setReferralDoctor(entity.getReferralDoctor());
        dto.setDepartment(entity.getDepartment());
        dto.setActive(Boolean.TRUE.equals(entity.getActive()));
        dto.setRegisteredAt(entity.getRegisteredAt());

        return dto;
    }
}
