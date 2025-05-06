package com.example.mdb.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(

        @NotNull
        @Min(1)
        @Max(10)
        int rating,

        @NotNull
        @Size(min = 5, max = 100, message = "Please provide a proper review")
        String review
) {}
