package com.example.mdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TheaterRegistrationRequest(

        @NotBlank(message = "Theater name is required")
        @Size(min = 3, max = 25, message = "Theater name cannot be blank")
        String name,

        @NotBlank(message = "Address is required")
        @Size(min = 5, max = 50, message = "Address cannot be blank")
        String address,

        @NotBlank(message = "City is required")
        @Size(min = 2, max = 25, message = "City cannot be blank")
        String city,

        @NotBlank(message = "Landmark is required")
        @Size(min = 3, max = 50, message = "Landmark cannot be blank")
        String landmark
) {}
