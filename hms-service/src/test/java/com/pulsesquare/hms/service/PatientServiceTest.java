package com.pulsesquare.hms.service;

import com.pulsesquare.hms.common.exception.BadRequestException;
import com.pulsesquare.hms.jooq.tables.pojos.HmsPatients;
import com.pulsesquare.hms.model.dto.PatientCreateRequest;
import com.pulsesquare.hms.model.dto.PatientDto;
import com.pulsesquare.hms.repo.repository.PatientRepository;
import com.pulsesquare.hms.service.impl.PatientServiceImpl;
import com.pulsesquare.hms.service.service.AuditService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Business Logic Unit Tests")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    @DisplayName("Should successfully register a new patient and assign generated MRN")
    void testRegisterPatientSuccess() {
        PatientCreateRequest req = new PatientCreateRequest();
        req.setTitle("Mr");
        req.setFirstName("Rahul");
        req.setLastName("Sharma");
        req.setGender("MALE");
        req.setDateOfBirth(LocalDate.of(1988, 6, 15));
        req.setAgeYears(38);
        req.setBloodGroup("O+");
        req.setContactNumber("+91-9876543210");
        req.setEmail("rahul.sharma@example.com");
        req.setAddress("A-42, Silicon Enclave");
        req.setCity("Nizamabad");
        req.setState("Telangana");
        req.setCountry("India");
        req.setZipCode("503001");
        req.setDepartment("Out-Patient");
        req.setRegistrationFee(new BigDecimal("100.00"));

        when(patientRepository.generateNextMrn()).thenReturn("MRN-2026-010001");
        when(patientRepository.save(any(HmsPatients.class))).thenAnswer(inv -> {
            HmsPatients p = inv.getArgument(0);
            p.setId(1001L);
            p.setRegisteredAt(LocalDateTime.now());
            return p;
        });

        PatientDto result = patientService.registerPatient(req, "127.0.0.1", "JUnit-Agent");

        assertNotNull(result);
        assertEquals(1001L, result.getId());
        assertEquals("MRN-2026-010001", result.getMrn());
        assertEquals("Rahul", result.getFirstName());
        assertEquals("Sharma", result.getLastName());
        assertEquals("Mr Rahul Sharma", result.getFullName());
        assertEquals("MALE", result.getGender());
        assertEquals("+91-9876543210", result.getContactNumber());

        verify(patientRepository).generateNextMrn();
        verify(patientRepository).save(any(HmsPatients.class));
        verify(auditService).logEvent(eq("PATIENT_REGISTERED"), eq("MRN-2026-010001"), eq("127.0.0.1"), eq("JUnit-Agent"), eq("SUCCESS"), anyString());
    }

    @Test
    @DisplayName("Should reject patient registration when mandatory fields are missing")
    void testRegisterPatientValidation() {
        PatientCreateRequest reqEmptyFirst = new PatientCreateRequest();
        reqEmptyFirst.setFirstName("");
        reqEmptyFirst.setLastName("Sharma");
        reqEmptyFirst.setGender("MALE");
        assertThrows(BadRequestException.class, () -> patientService.registerPatient(reqEmptyFirst, "127.0.0.1", "Agent"));

        PatientCreateRequest reqEmptyLast = new PatientCreateRequest();
        reqEmptyLast.setFirstName("Rahul");
        reqEmptyLast.setLastName("");
        reqEmptyLast.setGender("MALE");
        assertThrows(BadRequestException.class, () -> patientService.registerPatient(reqEmptyLast, "127.0.0.1", "Agent"));

        PatientCreateRequest reqEmptyGender = new PatientCreateRequest();
        reqEmptyGender.setFirstName("Rahul");
        reqEmptyGender.setLastName("Sharma");
        reqEmptyGender.setGender("");
        assertThrows(BadRequestException.class, () -> patientService.registerPatient(reqEmptyGender, "127.0.0.1", "Agent"));
    }

    @Test
    @DisplayName("Should lookup patient by MRN for universal cross-module access")
    void testGetPatientByMrn() {
        HmsPatients p = new HmsPatients();
        p.setId(1001L);
        p.setMrn("MRN-2026-010001");
        p.setTitle("Mr");
        p.setFirstName("Rahul");
        p.setLastName("Sharma");
        p.setGender("MALE");
        p.setContactNumber("+91-9876543210");
        p.setActive(true);

        when(patientRepository.findByMrn("MRN-2026-010001")).thenReturn(Optional.of(p));

        Optional<PatientDto> found = patientService.getPatientByMrn("MRN-2026-010001");
        assertTrue(found.isPresent());
        assertEquals(1001L, found.get().getId());
        assertEquals("MRN-2026-010001", found.get().getMrn());
        assertEquals("Rahul", found.get().getFirstName());

        Optional<PatientDto> notFound = patientService.getPatientByMrn("NON-EXISTENT");
        assertFalse(notFound.isPresent());
    }

    @Test
    @DisplayName("Should search patients by query across MRN, name, and phone")
    void testSearchPatients() {
        HmsPatients p = new HmsPatients();
        p.setId(1001L);
        p.setMrn("MRN-2026-010001");
        p.setTitle("Mr");
        p.setFirstName("Rahul");
        p.setLastName("Sharma");
        p.setGender("MALE");
        p.setContactNumber("+91-9876543210");

        when(patientRepository.search("Rahul", 0, 10)).thenReturn(List.of(p));
        when(patientRepository.countSearch("Rahul")).thenReturn(1L);

        List<PatientDto> results = patientService.searchPatients("Rahul", 0, 10);
        assertEquals(1, results.size());
        assertEquals("Rahul", results.get(0).getFirstName());
        assertEquals(1L, patientService.countPatients("Rahul"));
    }
}
