package com.horsetrust.api.dto;

import jakarta.validation.constraints.Size;

public record UpdateMeRequest(
        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phone,

        @Size(max = 1000, message = "Bio must not exceed 1000 characters")
        String bio,

        @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
        String profileImageUrl
) {}