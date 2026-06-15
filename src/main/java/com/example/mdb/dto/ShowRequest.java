package com.example.mdb.dto;

import com.example.mdb.enums.SeatCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ShowRequest(
        @NotBlank(message = "Movie ID is required")
        String movieId,

        @NotNull(message = "Start time is required")
        Long startTime,

        @NotBlank(message = "Zone ID is required")
        String zoneId,

        @NotEmpty(message = "Category pricing map is required")
        Map<SeatCategory, Double> categoryPrices
) {}