package com.horsetrust.api.dto;

import com.horsetrust.models.enums.UserRole;
import com.horsetrust.models.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        UserRole role,
        UserStatus status,
        String phone,
        String bio,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}