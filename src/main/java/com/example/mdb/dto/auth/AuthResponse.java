package com.example.mdb.dto.auth;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AuthResponse(

        String userId,
        String fullName,
        String username,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,
        String role,
        long accessExpiration,
        long refreshExpiration,
        String accessToken,
        String refreshToken
) {}
