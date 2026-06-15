package com.example.mdb.dto;

import com.example.mdb.enums.SeatCategory;
import lombok.Builder;

@Builder
public record SeatStatusResponse(
        String seatId,
        String seatName,
        boolean isBooked,
        SeatCategory seatCategory,
        Double price
) {}