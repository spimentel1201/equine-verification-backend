package com.horsetrust.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EvidenceResponse(
        UUID id,
        String type,
        String status,
        String fileUrl,
        String description,
        String metadata,
        UUID listingId,
        UUID horseId,
        UUID uploaderId,
        LocalDateTime uploadedAt) {
}
