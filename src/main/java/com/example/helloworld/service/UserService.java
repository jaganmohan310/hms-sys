package com.example.helloworld.service;

import com.example.helloworld.dto.LoginRequest;
import com.example.helloworld.dto.LoginResponse;
import com.example.helloworld.model.User;
import com.example.helloworld.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedInitialUsers() {
        try {
            if (userRepository.count() == 0) {
                logger.info("Initializing enterprise default users in database...");
                userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "Hospital Administrator", "admin@pulsesquare.com", "ADMIN"));
                userRepository.save(new User("dr_sharma", passwordEncoder.encode("doctor123"), "Dr. Rajesh Sharma (Cardiology)", "dr.sharma@pulsesquare.com", "DOCTOR"));
                userRepository.save(new User("patient_john", passwordEncoder.encode("patient123"), "John Doe", "john.doe@gmail.com", "PATIENT"));
                userRepository.save(new User("nurse_sarah", passwordEncoder.encode("nurse123"), "Sarah Jenkins (ICU Care)", "sarah.j@pulsesquare.com", "STAFF"));
                logger.info("Enterprise default users successfully seeded.");
            }
        } catch (Exception e) {
            logger.warn("Could not pre-seed users (Database may be read-only or initializing): {}", e.getMessage());
        }
    }

    public LoginResponse authenticate(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return LoginResponse.failure("Username and password are required");
        }

        Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(request.getUsername().trim());
        if (!userOpt.isPresent()) {
            return LoginResponse.failure("Invalid username or password");
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            return LoginResponse.failure("Account is inactive. Please contact hospital administrator.");
        }

        String rawPassword = request.getPassword().trim();
        boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword()) || user.getPassword().equals(rawPassword);

        if (!passwordMatches) {
            return LoginResponse.failure("Invalid username or password");
        }

        // Auto-upgrade plain password to BCrypt hash if not already hashed
        if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        }

        // Generate enterprise session token
        String token = "HMS-SEC-" + UUID.randomUUID().toString().toUpperCase();

        return LoginResponse.success(
                "Authentication successful. Welcome to PulseSquare HMS.",
                token,
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already registered");
        }
        // Securely hash password with BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        user.setActive(true);
        return userRepository.save(user);
    }
}
