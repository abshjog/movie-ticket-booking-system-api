package com.example.mdb.dto;

import java.time.LocalDate;

public record UserRegistrationRequest(
        String email,
        String username,
        String password,
        String userRole,
        LocalDate dateOfBirth,
        String phoneNumber
) {}
