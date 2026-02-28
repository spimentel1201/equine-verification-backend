package com.horsetrust.api.dto;

import com.horsetrust.models.enums.VerificationStatus;
import jakarta.validation.constraints.*;

public record ReviewVerificationRequest(
        @NotNull(message = "Status is required") VerificationStatus status,
        @Size(max = 2000) String notes) {
}
