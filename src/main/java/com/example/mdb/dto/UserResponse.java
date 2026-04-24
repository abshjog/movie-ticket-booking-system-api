package com.example.mdb.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        String userId,
        String fullName,
        String username,
        String email,
        String userRole,
        String phoneNumber
) {}
