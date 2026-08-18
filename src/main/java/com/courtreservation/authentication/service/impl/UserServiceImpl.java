package com.courtreservation.authentication.service.impl;

import com.courtreservation.authentication.dto.LoginRequest;
import com.courtreservation.authentication.dto.LoginResponse;
import com.courtreservation.authentication.dto.RegisterRequest;
import com.courtreservation.authentication.dto.UserResponse;
import com.courtreservation.authentication.model.User;
import com.courtreservation.authentication.repository.UserRepository;
import com.courtreservation.authentication.security.JwtTokenProvider;
import com.courtreservation.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Set<String> VALID_ROLES = Set.of("ADMIN", "USUARIO_FINAL");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUserMail(request.getUserMail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = User.builder()
                .userName(request.getUserName())
                .userMail(request.getUserMail())
                .userPassword(passwordEncoder.encode(request.getUserPassword()))
                .userRole(resolveRole(request.getUserRole()))
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUserMail(request.getUserMail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUserMail(), user.getUserRole());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userMail(user.getUserMail())
                .userRole(user.getUserRole())
                .build();
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getUserPassword() != null) {
            user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        }
        if (request.getUserRole() != null) {
            user.setUserRole(validateRole(request.getUserRole()));
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(userId);
    }

    public UserResponse changeRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setUserRole(validateRole(newRole));
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    private String resolveRole(String role) {
        if (role == null || role.isBlank()) {
            return "USUARIO_FINAL";
        }
        return validateRole(role);
    }

    private String validateRole(String role) {
        String normalizedRole = role.trim().toUpperCase();
        if (!VALID_ROLES.contains(normalizedRole)) {
            throw new RuntimeException("Rol inválido. Debe ser ADMIN o USUARIO_FINAL");
        }
        return normalizedRole;
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userMail(user.getUserMail())
                .userRole(user.getUserRole())
                .build();
    }
}
