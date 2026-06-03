package com.example.mdb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserUpdationRequest(

        @NotNull(message = "Full name must not be null")
        @Pattern(
                regexp = "^[a-zA-Z\\s]{3,50}$",
                message = "Full name must be 3-50 characters long and contain only letters and spaces"
        )
        String fullName,

        @NotNull
        @Pattern(regexp = "^(?=.{3,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z][a-zA-Z0-9._]*[a-zA-Z0-9]$",
                message = "Username should be minimum of 3 and maximum of 20 alpha-numeric character and can have special characters . and _")
        String username,

        @NotNull
        @Email(message = "Invalid Email format")
        String email,

        @NotNull
        @Pattern(regexp = "^[6-9]\\d{9}$",
                message = "Phone number must be a valid 10-digit Indian mobile number")
        String phoneNumber,

        @NotNull
        @Past(message = "Date of birth must be a past date")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender must not be null")
        @Pattern(
                regexp = "^(Male|Female|Others|Prefer not to say)$",
                message = "Gender must be 'Male', 'Female', 'Others', or 'Prefer not to say'"
        )
        String gender
) {}