package com.horsetrust.api.dto;

import com.horsetrust.models.entities.User;
import com.horsetrust.models.enums.UserRole;

import java.util.UUID;

public record UserResponse(UUID id, String email, UserRole role, boolean active) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getRole(), u.getStatus().name().equals("ACTIVE"));
    }
}