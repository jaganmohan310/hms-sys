package com.pulsesquare.hms.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulsesquare.hms.model.dto.PatientCreateRequest;
import com.pulsesquare.hms.model.dto.PatientDto;
import com.pulsesquare.hms.service.security.JwtTokenProvider;
import com.pulsesquare.hms.service.service.PatientService;
import com.pulsesquare.hms.ws.controller.PatientController;
import com.pulsesquare.hms.ws.security.JwtAuthenticationFilter;
import com.pulsesquare.hms.ws.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("PatientController REST Endpoint Tests")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = "valid.jwt.token";
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(validToken)).thenReturn("receptionist1");
        when(jwtTokenProvider.getRoleFromToken(validToken)).thenReturn("RECEPTIONIST");
    }

    @Test
    @DisplayName("POST /api/v1/patients - Register patient returns 201 Created with generated MRN")
    void testRegisterPatientSuccess() throws Exception {
        PatientCreateRequest req = new PatientCreateRequest();
        req.setTitle("Mr");
        req.setFirstName("Rahul");
        req.setLastName("Sharma");
        req.setGender("MALE");
        req.setDateOfBirth(LocalDate.of(1988, 6, 15));
        req.setContactNumber("+91-9876543210");
        req.setBloodGroup("O+");
        req.setAddress("A-42, Silicon Enclave");
        req.setCity("Nizamabad");
        req.setState("Telangana");
        req.setCountry("India");
        req.setZipCode("503001");
        req.setRegistrationFee(new BigDecimal("100.00"));

        PatientDto dto = new PatientDto();
        dto.setId(1001L);
        dto.setMrn("MRN-2026-010001");
        dto.setTitle("Mr");
        dto.setFirstName("Rahul");
        dto.setLastName("Sharma");
        dto.setGender("MALE");
        dto.setContactNumber("+91-9876543210");
        dto.setActive(true);

        when(patientService.registerPatient(any(PatientCreateRequest.class), anyString(), anyString())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/patients")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mrn").value("MRN-2026-010001"))
                .andExpect(jsonPath("$.data.firstName").value("Rahul"))
                .andExpect(jsonPath("$.data.lastName").value("Sharma"));
    }

    @Test
    @DisplayName("GET /api/v1/patients/mrn/{mrn} - Returns 200 OK with patient record")
    void testGetPatientByMrnSuccess() throws Exception {
        PatientDto dto = new PatientDto();
        dto.setId(1001L);
        dto.setMrn("MRN-2026-010001");
        dto.setFirstName("Rahul");
        dto.setLastName("Sharma");
        dto.setGender("MALE");
        dto.setContactNumber("+91-9876543210");

        when(patientService.getPatientByMrn("MRN-2026-010001")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/patients/mrn/MRN-2026-010001")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mrn").value("MRN-2026-010001"))
                .andExpect(jsonPath("$.data.firstName").value("Rahul"));
    }

    @Test
    @DisplayName("GET /api/v1/patients/mrn/{mrn} - Returns 404 when MRN does not exist")
    void testGetPatientByMrnNotFound() throws Exception {
        when(patientService.getPatientByMrn("NON-EXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/patients/mrn/NON-EXISTENT")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/patients?query=Rahul - Searches and returns patient list")
    void testListPatientsSearch() throws Exception {
        PatientDto dto = new PatientDto();
        dto.setId(1001L);
        dto.setMrn("MRN-2026-010001");
        dto.setFirstName("Rahul");
        dto.setLastName("Sharma");

        when(patientService.searchPatients("Rahul", 0, 50)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/patients")
                        .param("query", "Rahul")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].mrn").value("MRN-2026-010001"))
                .andExpect(jsonPath("$.data[0].firstName").value("Rahul"));
    }
}
