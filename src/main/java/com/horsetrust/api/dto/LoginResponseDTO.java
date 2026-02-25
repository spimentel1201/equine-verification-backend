package com.horsetrust.api.dto;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        UserResponse user
) {}