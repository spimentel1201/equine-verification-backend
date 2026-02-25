package com.horsetrust.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserMeResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String role,
        String status,
        String phone,
        String profileImageUrl,
        String bio,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}