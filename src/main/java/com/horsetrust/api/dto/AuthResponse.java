package com.horsetrust.api.dto;

import com.horsetrust.models.entities.User;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID id,
        String role,
        String email,
        String firstName,
        String lastName
) {
    public static AuthResponse from(User u, String token) {
        return new AuthResponse(
                token,
                u.getId(),
                u.getRole().name(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName()
        );
    }
}