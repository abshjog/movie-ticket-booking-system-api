package com.example.mdb.dto.auth;

import lombok.Builder;

@Builder
public record AuthResponse(

        String userId,
        String username,
        String email,
        String role,
        long accessExpiration,
        long refreshExpiration,
        String accessToken,
        String refreshToken
) {}
