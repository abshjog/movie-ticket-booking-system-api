package com.example.mdb.dto;

import lombok.Builder;

@Builder
public record SeatStatusResponse(
        String seatId,
        String seatName,
        boolean isBooked
) {}
