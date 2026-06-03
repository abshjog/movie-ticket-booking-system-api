package com.example.mdb.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse(
        String userId,
        String fullName,
        String username,
        String email,
        String userRole,
        String phoneNumber,
        LocalDate dateOfBirth,
        String gender
) {}
