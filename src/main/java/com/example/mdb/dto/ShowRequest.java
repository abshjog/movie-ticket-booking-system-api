package com.example.mdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ShowRequest(
        @NotBlank(message = "Movie ID is required")
        String movieId,

        @NotNull(message = "Start time is required")
        Long startTime,

        @NotBlank(message = "Zone ID is required")
        String zoneId,

        @Positive(message = "Ticket price must be greater than zero")
        Double ticketPrice
) {}
