package com.horsetrust.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RequestVerificationRequest(
        @NotNull(message = "Listing ID is required") UUID listingId) {
}
