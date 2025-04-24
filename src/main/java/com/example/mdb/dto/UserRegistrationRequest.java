package com.example.mdb.dto;

import com.example.mdb.enums.UserRole;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRegistrationRequest(

        @NotBlank(message = "Username must not be blank")
        @Pattern(
                regexp = "^[a-zA-Z0-9@]{3,20}$",
                message = "Username must be 3-20 characters long and can only contain alphanumeric characters and '@'"
        )
        String username,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "Password must be between 8-20 characters and include at least one lowercase letter, one uppercase letter, one digit, and one special character (@$!%*?&)"
        )
        String password,

        @NotBlank(message = "Phone number must not be blank")
        @Pattern(
                regexp = "^[6-9]\\d{9}$",
                message = "Phone number must be a valid 10-digit Indian mobile number"
        )
        String phoneNumber,

        @NotNull(message = "User role must not be null")
        UserRole userRole,

        @NotNull(message = "Date of birth must not be null")
        @Past(message = "Date of birth must be a past date")
        LocalDate dateOfBirth
) {}
