package com.horsetrust.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ListingResponse(
        UUID id,
        UUID horseId,
        UUID sellerId,
        String title,
        String description,
        String conditions,
        BigDecimal price,
        String location,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}