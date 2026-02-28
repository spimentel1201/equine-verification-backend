package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Módulo de autenticación de usuarios.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta de usuario. Devuelve access y refresh token.")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Registrar nuevo administrador", description = "Crea una cuenta de usuario administrador. Exclusivo para administradores.")
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.registerAdmin(req));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica con email y password. Usa el accessToken en 'Authorize'.")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Genera un nuevo accessToken a partir de un refreshToken válido.")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}