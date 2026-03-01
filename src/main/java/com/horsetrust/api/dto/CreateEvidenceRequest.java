package com.horsetrust.api.dto;

import com.horsetrust.models.enums.EvidenceType;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateEvidenceRequest(
                @NotNull(message = "Evidence type is required") EvidenceType type,
                @Size(max = 500) String description,
                @Size(max = 2000) String metadata,
                UUID listingId,
                UUID horseId) {
}
