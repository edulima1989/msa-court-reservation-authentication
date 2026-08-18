package com.courtreservation.authentication.service;

import com.courtreservation.authentication.dto.LoginRequest;
import com.courtreservation.authentication.dto.LoginResponse;
import com.courtreservation.authentication.dto.RegisterRequest;
import com.courtreservation.authentication.dto.TokenValidationResponse;
import com.courtreservation.authentication.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    TokenValidationResponse validateSessionToken(String token);

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long userId, RegisterRequest request);

    void deleteUser(Long userId);

    UserResponse changeRole(Long userId, String newRole);
}
