package com.horsetrust.api.dto;

import com.horsetrust.models.enums.HorseGender;
import java.time.LocalDateTime;
import java.util.UUID;

public record HorseResponse(
        UUID id,
        String name,
        String breed,
        Integer age,
        HorseGender gender,
        UUID ownerId,
        LocalDateTime createdAt
) {}