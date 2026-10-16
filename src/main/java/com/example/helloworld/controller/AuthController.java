package com.example.helloworld.controller;

import com.example.helloworld.config.DataSourceConfig;
import com.example.helloworld.dto.ApiResponse;
import com.example.helloworld.dto.LoginRequest;
import com.example.helloworld.dto.LoginResponse;
import com.example.helloworld.dto.RegisterRequest;
import com.example.helloworld.model.User;
import com.example.helloworld.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/auth", "/api/v1/auth"})
public class AuthController {

    private final UserService userService;
    private final DataSourceConfig dataSourceConfig;

    @Autowired
    public AuthController(UserService userService, DataSourceConfig dataSourceConfig) {
        this.userService = userService;
        this.dataSourceConfig = dataSourceConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.authenticate(loginRequest);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User newUser = new User(
                    request.getUsername().trim(),
                    request.getPassword().trim(),
                    request.getFullName().trim(),
                    request.getEmail().trim(),
                    request.getRole().toUpperCase().trim()
            );
            User saved = userService.registerUser(newUser);
            return ResponseEntity.ok(ApiResponse.ok("Registration successful! You can now sign in with your credentials.", saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.ok("User session terminated successfully", null));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok("Fetched users successfully", users));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "PulseSquare Auth Service");
        status.put("database", "IBM Db2");
        status.put("mode", dataSourceConfig.isUsingFallback() ? "EMBEDDED_SIMULATION" : "LIVE_SERVER");
        status.put("status", "UP");
        return ResponseEntity.ok(status);
    }
}
