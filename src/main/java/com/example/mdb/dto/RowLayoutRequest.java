package com.example.mdb.dto;

import com.example.mdb.enums.SeatCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RowLayoutRequest(
        @NotBlank(message = "Row label is required (e.g., A)")
        String rowLabel,

        @NotNull(message = "Category is required for the row")
        SeatCategory category,

        @NotEmpty(message = "Seats array cannot be empty")
        List<String> seats
) {}
