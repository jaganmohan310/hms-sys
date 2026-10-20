package com.pulsesquare.hms.service.service;

import com.pulsesquare.hms.model.dto.AuthResponse;
import com.pulsesquare.hms.model.dto.LoginRequest;
import com.pulsesquare.hms.model.dto.SignupRequest;
import com.pulsesquare.hms.model.dto.UserDto;

public interface AuthService {

    AuthResponse authenticate(LoginRequest request, String ipAddress, String userAgent);

    UserDto register(SignupRequest request, String ipAddress, String userAgent);

    UserDto getUserProfile(String username);

    UserDto validateAndGetUser(String token);

    void logout(String token, String ipAddress, String userAgent);
}
