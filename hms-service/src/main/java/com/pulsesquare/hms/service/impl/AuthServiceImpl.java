package com.pulsesquare.hms.service.impl;

import com.pulsesquare.hms.common.exception.DuplicateResourceException;
import com.pulsesquare.hms.common.exception.InvalidCredentialsException;
import com.pulsesquare.hms.common.exception.ResourceNotFoundException;
import com.pulsesquare.hms.jooq.tables.pojos.HmsRoles;
import com.pulsesquare.hms.jooq.tables.pojos.HmsUsers;
import com.pulsesquare.hms.model.dto.AuthResponse;
import com.pulsesquare.hms.model.dto.LoginRequest;
import com.pulsesquare.hms.model.dto.SignupRequest;
import com.pulsesquare.hms.model.dto.UserDto;
import com.pulsesquare.hms.repo.repository.RoleRepository;
import com.pulsesquare.hms.repo.repository.UserRepository;
import com.pulsesquare.hms.service.security.JwtTokenProvider;
import com.pulsesquare.hms.service.security.PasswordService;
import com.pulsesquare.hms.service.service.AuditService;
import com.pulsesquare.hms.service.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditService auditService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordService passwordService,
                           JwtTokenProvider jwtTokenProvider,
                           AuditService auditService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordService = passwordService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.auditService = auditService;
    }

    @Override
    public AuthResponse authenticate(LoginRequest request, String ipAddress, String userAgent) {
        String identifier = request.getUsername() != null ? request.getUsername().trim() : "";
        String rawPassword = request.getPassword() != null ? request.getPassword().trim() : "";

        if (identifier.isEmpty() || rawPassword.isEmpty()) {
            auditService.logEvent("LOGIN_ATTEMPT_EMPTY", identifier, ipAddress, userAgent, "FAILURE", "Missing credentials");
            return AuthResponse.failure("Username and password are required");
        }

        Optional<HmsUsers> userOpt = userRepository.findByUsernameOrEmail(identifier, identifier);
        if (!userOpt.isPresent()) {
            auditService.logEvent("LOGIN_FAILURE", identifier, ipAddress, userAgent, "FAILURE", "User not found");
            return AuthResponse.failure("Invalid username or password");
        }

        HmsUsers user = userOpt.get();

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            auditService.logEvent("LOGIN_BLOCKED", identifier, ipAddress, userAgent, "FAILURE", "Account is locked");
            return AuthResponse.failure("Account has been locked due to multiple failed login attempts. Contact administrator.");
        }

        if (!Boolean.TRUE.equals(user.getAccountEnabled())) {
            auditService.logEvent("LOGIN_DISABLED", identifier, ipAddress, userAgent, "FAILURE", "Account disabled");
            return AuthResponse.failure("Account is disabled. Please contact your hospital administrator.");
        }

        boolean passwordValid = passwordService.matches(rawPassword, user.getPasswordHash());

        if (!passwordValid) {
            int failedAttempts = (user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0) + 1;
            user.setFailedLoginAttempts(failedAttempts);
            if (failedAttempts >= 5) {
                user.setAccountLocked(true);
                logger.warn("User account locked due to excessive failed attempts: {}", user.getUsername());
            }
            userRepository.save(user);
            auditService.logEvent("LOGIN_FAILURE", identifier, ipAddress, userAgent, "FAILURE", "Invalid password attempt #" + failedAttempts);
            return AuthResponse.failure("Invalid username or password");
        }

        // Reset failed login attempts on successful authentication
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());

        // Transparently auto-upgrade plain-text or outdated hash to current BCrypt cost factor
        if (!user.getPasswordHash().startsWith("$2a$") && !user.getPasswordHash().startsWith("$2b$")) {
            user.setPasswordHash(passwordService.encode(rawPassword));
        }
        userRepository.save(user);

        Set<HmsRoles> roles = userRepository.findRolesByUserId(user.getId());

        // Derive primary role
        String primaryRole = roles.stream()
                .map(HmsRoles::getName)
                .findFirst()
                .orElse("ROLE_PATIENT");

        // Clean role representation for UI (remove "ROLE_" prefix if desired)
        String roleCode = primaryRole.replace("ROLE_", "");

        // Issue stateless JWT token
        String token = jwtTokenProvider.generateToken(user.getUsername(), roleCode, user.getFullName(), user.getEmail());
        long expiresIn = jwtTokenProvider.getExpirationMs();

        UserDto userDto = mapToUserDto(user, roles);

        auditService.logEvent("LOGIN_SUCCESS", user.getUsername(), ipAddress, userAgent, "SUCCESS", "Role: " + roleCode);
        logger.info("Enterprise user authenticated successfully: {} (Role: {})", user.getUsername(), roleCode);

        return AuthResponse.success("Authentication successful. Welcome to PulseSquare HMS.", token, expiresIn, userDto);
    }

    @Override
    public UserDto register(SignupRequest request, String ipAddress, String userAgent) {
        String username = request.getUsername().trim().toLowerCase();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            auditService.logEvent("SIGNUP_REJECTED", username, ipAddress, userAgent, "FAILURE", "Username duplicate");
            throw new DuplicateResourceException("Username '" + username + "' is already registered");
        }

        if (userRepository.existsByEmail(email)) {
            auditService.logEvent("SIGNUP_REJECTED", email, ipAddress, userAgent, "FAILURE", "Email duplicate");
            throw new DuplicateResourceException("Email address '" + email + "' is already registered");
        }

        // Standardize Role
        String rawRole = request.getRole() != null ? request.getRole().trim().toUpperCase() : "PATIENT";
        String normalizedRoleName = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;

        HmsRoles role = roleRepository.findByName(normalizedRoleName)
                .orElseGet(() -> {
                    HmsRoles newRole = new HmsRoles();
                    newRole.setName(normalizedRoleName);
                    newRole.setDescription("Self-registered " + rawRole);
                    newRole.setCreatedAt(LocalDateTime.now());
                    return roleRepository.save(newRole);
                });

        HmsUsers user = new HmsUsers();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordService.encode(request.getPassword().trim()));
        user.setFullName(request.getFullName().trim());
        user.setContactNumber(request.getContactNumber());
        user.setDepartment(request.getDepartment());
        user.setAccountEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Set<HmsRoles> userRoles = new HashSet<>(Collections.singletonList(role));
        HmsUsers savedUser = userRepository.save(user, userRoles);

        auditService.logEvent("SIGNUP_SUCCESS", savedUser.getUsername(), ipAddress, userAgent, "SUCCESS", "Assigned Role: " + normalizedRoleName);
        logger.info("New enterprise user registered: {} (Role: {})", savedUser.getUsername(), normalizedRoleName);

        return mapToUserDto(savedUser, userRoles);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserProfile(String username) {
        HmsUsers user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Set<HmsRoles> roles = userRepository.findRolesByUserId(user.getId());
        return mapToUserDto(user, roles);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto validateAndGetUser(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidCredentialsException("Invalid or expired JWT token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        return getUserProfile(username);
    }

    @Override
    public void logout(String token, String ipAddress, String userAgent) {
        String username = "ANONYMOUS";
        if (token != null) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String extracted = jwtTokenProvider.getUsernameFromToken(token);
            if (extracted != null && !extracted.trim().isEmpty()) {
                username = extracted.trim();
            }
        }

        auditService.logEvent("LOGOUT", username, ipAddress, userAgent, "SUCCESS", "Session ended");
        logger.info("Enterprise user logged out: {}", username);
    }

    private UserDto mapToUserDto(HmsUsers user, Set<HmsRoles> roles) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setDepartment(user.getDepartment());
        dto.setContactNumber(user.getContactNumber());
        dto.setAccountEnabled(Boolean.TRUE.equals(user.getAccountEnabled()));
        dto.setCreatedAt(user.getCreatedAt());

        Set<String> roleNames = roles != null ? roles.stream()
                .map(HmsRoles::getName)
                .collect(Collectors.toSet()) : Collections.emptySet();
        dto.setRoles(roleNames);

        String primaryRole = roleNames.stream().findFirst().orElse("ROLE_PATIENT").replace("ROLE_", "");
        dto.setRole(primaryRole);

        return dto;
    }
}
