package com.example.mdb.dto;

public record UserRequest(
        String username,
        String phoneNumber,
        String email
) {}
