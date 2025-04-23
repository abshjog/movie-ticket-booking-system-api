package com.example.mdb.dto;

import java.time.LocalDate;

public record UserResponse(
        String id,
        String username,
        String email,
        String role,
        LocalDate dateOfBirth,
        String phoneNumber
) {}
