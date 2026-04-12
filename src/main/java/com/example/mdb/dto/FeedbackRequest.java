package com.example.mdb.dto;

import jakarta.validation.constraints.*;

public record FeedbackRequest(

        @Min(value = 1, message = "Rating must be at least 1.")
        @Max(value = 10, message = "Rating cannot exceed 10.")
        int rating,

        @NotBlank(message = "Review cannot be blank.")
        @Size(min = 5, max = 100, message = "Review must be between 5 and 100 characters.")
        String review
) {}
