package com.example.mdb.dto;

import com.example.mdb.enums.ScreenType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ScreenRequest(

        @NotBlank(message = "Screen name is required (e.g., Audi 1)")
        String screenName,

        @NotNull(message = "Screen type is required")
        ScreenType screenType,

        @NotEmpty(message = "Seat layout cannot be empty")
        List<RowLayoutRequest> seatLayout
) {}