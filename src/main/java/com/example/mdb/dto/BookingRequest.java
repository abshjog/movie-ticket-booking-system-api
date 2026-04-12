package com.example.mdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(
        @NotBlank(message = "Show ID cannot be blank")
        String showId,

        @NotNull(message = "Seat list cannot be null")
        @NotEmpty(message = "Please select at least one seat")
        List<String> seatIds
) {}
