package com.pulsesquare.hms.ws.controller;

import com.pulsesquare.hms.common.model.ApiResponse;
import com.pulsesquare.hms.model.dto.PatientCreateRequest;
import com.pulsesquare.hms.model.dto.PatientDto;
import com.pulsesquare.hms.service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/v1/patients", "/api/patients"})
@Tag(name = "Patient Administration", description = "Master Patient Index (MPI), demographic records, registration, and patient lookup")
@SecurityRequirement(name = "BearerAuth")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @Operation(summary = "Register a new patient", description = "Creates a master patient index (MPI) entry and generates an enterprise Medical Record Number (MRN).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Patient registered successfully with generated MRN"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failure or invalid patient input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT bearer token")
    })
    public ResponseEntity<ApiResponse<PatientDto>> registerPatient(@Valid @RequestBody PatientCreateRequest request) {
        HttpServletRequest httpRequest = getCurrentHttpRequest();
        String ipAddress = extractClientIp(httpRequest);
        String userAgent = (httpRequest != null && httpRequest.getHeader("User-Agent") != null) ? httpRequest.getHeader("User-Agent") : "";

        PatientDto registered = patientService.registerPatient(request, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Patient registered successfully with MRN: " + registered.getMrn(), registered));
    }

    @GetMapping("/mrn/{mrn}")
    @Operation(summary = "Retrieve patient details by MR Number (MRN)", description = "Lookup master patient record by unique Medical Record Number across Out-Patient, Soft Lab, and In-Patient care modules.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient record retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found for the specified MRN"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<PatientDto>> getPatientByMrn(
            @Parameter(description = "Medical Record Number (e.g. MRN-2026-010001)", required = true)
            @PathVariable("mrn") String mrn) {

        Optional<PatientDto> patientOpt = patientService.getPatientByMrn(mrn);
        if (patientOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.ok("Patient record retrieved", patientOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("Patient not found with MRN: " + mrn));
        }
    }

    @GetMapping
    @Operation(summary = "Search and list patient medical records", description = "Retrieve a list of registered patients matching search criteria (MRN, Name, Mobile) with pagination.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list of patient records"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<PatientDto>>> listPatients(
            @Parameter(description = "Search query matching MRN, full name, or contact number")
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "Zero-based page index")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Number of records per page")
            @RequestParam(value = "size", defaultValue = "50") int size) {

        List<PatientDto> patients = patientService.searchPatients(query, page, size);
        return ResponseEntity.ok(ApiResponse.ok("Patient records retrieved", patients));
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "127.0.0.1";
    }
}
