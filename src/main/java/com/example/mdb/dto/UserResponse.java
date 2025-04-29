package com.example.mdb.dto;

import java.time.LocalDate;

public record UserResponse(
        String userId,
        String username,
        String email,
        String userRole,
        String phoneNumber
) {}
