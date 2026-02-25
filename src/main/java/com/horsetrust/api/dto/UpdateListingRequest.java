package com.horsetrust.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateListingRequest(
        @DecimalMin(value = "0.01") BigDecimal price,
        @Size(max = 200) String location,
        @Size(min = 20, max = 5000) String description,
        @Size(max = 2000) String conditions
) {}