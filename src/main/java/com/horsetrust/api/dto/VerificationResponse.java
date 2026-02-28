package com.horsetrust.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record VerificationResponse(
        UUID id,
        String target,
        UUID targetId,
        String status,
        String notes,
        UUID verifierId,
        LocalDateTime validUntil,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
