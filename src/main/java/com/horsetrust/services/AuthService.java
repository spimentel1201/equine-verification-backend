package com.horsetrust.services;

import com.horsetrust.api.dto.*;
import com.horsetrust.models.entities.User;
import com.horsetrust.models.enums.UserRole;
import com.horsetrust.models.enums.UserStatus;
import com.horsetrust.repositories.UserRepository;
import com.horsetrust.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Transactional
    public LoginResponseDTO register(RegisterRequest req) {
        if (req.role() != UserRole.BUYER && req.role() != UserRole.SELLER) {
            throw new IllegalArgumentException("Role must be BUYER or SELLER");
        }
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        User saved = userRepository.save(User.builder()
                .email(email)
                .password(encoder.encode(req.password()))
                .firstName(req.firstName().trim())
                .lastName(req.lastName().trim())
                .role(req.role())
                .status(UserStatus.ACTIVE)
                .build());

        String access = tokenService.generarAccessToken(saved);
        String refresh = tokenService.generarRefreshToken(saved);

        return new LoginResponseDTO(access, refresh, UserResponse.from(saved));
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, req.password())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found after auth"));

        String access = tokenService.generarAccessToken(user);
        String refresh = tokenService.generarRefreshToken(user);

        return new LoginResponseDTO(access, refresh, UserResponse.from(user));
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO refresh(String refreshToken) {
        String type = tokenService.getTokenType(refreshToken);
        if (!"REFRESH".equals(type)) {
            throw new IllegalArgumentException("Token is not REFRESH");
        }

        String email = tokenService.getSubject(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("User not active");
        }

        String access = tokenService.generarAccessToken(user);
        String refresh = tokenService.generarRefreshToken(user);

        return new LoginResponseDTO(access, refresh, UserResponse.from(user));
    }
}