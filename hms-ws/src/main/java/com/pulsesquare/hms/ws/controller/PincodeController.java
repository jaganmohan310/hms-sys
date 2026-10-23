package com.pulsesquare.hms.ws.controller;

import com.pulsesquare.hms.common.model.ApiResponse;
import com.pulsesquare.hms.model.dto.PincodeDto;
import com.pulsesquare.hms.model.dto.PincodeLookupResponse;
import com.pulsesquare.hms.service.service.PincodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/v1/pincodes", "/api/pincodes"})
@Tag(name = "Indian Postal Directory & Geography", description = "Official All-India PIN code database, States, Districts, and Post Offices")
public class PincodeController {

    private final PincodeService pincodeService;

    @Autowired
    public PincodeController(PincodeService pincodeService) {
        this.pincodeService = pincodeService;
    }

    @GetMapping("/{pincode}")
    @Operation(summary = "Lookup official geographic details by PIN code", description = "Resolves a 6-digit Indian PIN code to its State, District, primary Division/Taluk, and associated Post Offices.")
    public ResponseEntity<ApiResponse<PincodeLookupResponse>> getPincodeDetails(
            @Parameter(description = "6-digit Indian PIN code (e.g. 503001, 110001, 560001)", required = true)
            @PathVariable("pincode") String pincode) {

        Optional<PincodeLookupResponse> result = pincodeService.lookup(pincode);
        if (result.isPresent()) {
            return ResponseEntity.ok(ApiResponse.ok("PIN code details resolved", result.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("PIN code not found in official directory: " + pincode));
        }
    }

    @GetMapping("/states")
    @Operation(summary = "List all Indian States and Union Territories", description = "Retrieves all distinct States and Union Territories from the official database.")
    public ResponseEntity<ApiResponse<List<String>>> getStates() {
        List<String> states = pincodeService.getStates();
        return ResponseEntity.ok(ApiResponse.ok("States retrieved successfully", states));
    }

    @GetMapping("/districts")
    @Operation(summary = "List districts for a State", description = "Retrieves all official districts under the specified Indian State or Union Territory.")
    public ResponseEntity<ApiResponse<List<String>>> getDistricts(
            @Parameter(description = "State name (e.g. TELANGANA, MAHARASHTRA, KARNATAKA)", required = true)
            @RequestParam("state") String state) {

        List<String> districts = pincodeService.getDistricts(state);
        return ResponseEntity.ok(ApiResponse.ok("Districts retrieved successfully", districts));
    }

    @GetMapping("/offices")
    @Operation(summary = "List post offices / areas for a District", description = "Retrieves post offices and localities for the specified district and state.")
    public ResponseEntity<ApiResponse<List<String>>> getOffices(
            @Parameter(description = "State name (optional)")
            @RequestParam(value = "state", required = false) String state,
            @Parameter(description = "District name (e.g. NIZAMABAD, HYDERABAD, PUNE)", required = true)
            @RequestParam("district") String district) {

        List<String> offices = pincodeService.getOffices(state, district);
        return ResponseEntity.ok(ApiResponse.ok("Offices retrieved successfully", offices));
    }

    @GetMapping("/search")
    @Operation(summary = "Search PIN codes, offices, and districts", description = "Live query search across official Indian postal records.")
    public ResponseEntity<ApiResponse<List<PincodeDto>>> search(
            @Parameter(description = "Search query string", required = true)
            @RequestParam("query") String query,
            @Parameter(description = "Maximum results to return")
            @RequestParam(value = "limit", defaultValue = "25") int limit) {

        List<PincodeDto> results = pincodeService.search(query, limit);
        return ResponseEntity.ok(ApiResponse.ok("Search results retrieved", results));
    }

    @PostMapping("/import")
    @Operation(summary = "Trigger official CSV database import", description = "Manually triggers background ingestion of pincodes.csv if not already loaded.")
    public ResponseEntity<ApiResponse<String>> triggerImport() {
        int loaded = pincodeService.loadPincodesFromCsvIfEmpty();
        return ResponseEntity.ok(ApiResponse.ok("Official PIN code import processed: " + loaded + " records present in database."));
    }
}
