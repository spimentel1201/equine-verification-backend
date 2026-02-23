package com.horsetrust.api.dto;

import com.horsetrust.models.enums.HorseGender;
import jakarta.validation.constraints.*;

public record CreateHorseRequest(
        @NotBlank @Size(min = 2, max = 100) String name,

        @NotBlank
        @Size(min = 2, max = 100)
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Breed contains invalid characters")
        String breed,

        @NotNull @Min(0) @Max(50) Integer age,

        @NotNull HorseGender gender
        // discipline: si lo agregás al entity, lo sumás acá
) {}