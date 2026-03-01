package com.horsetrust.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record HorsePhotoResponse(
        UUID id,
        String imageUrl,
        Integer displayOrder,
        LocalDateTime uploadedAt) {
}
