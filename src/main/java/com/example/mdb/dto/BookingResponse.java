package com.example.mdb.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
@Builder
public record BookingResponse(
        String bookingId,
        double totalAmount,
        String status,
        String movieTitle,
        String theaterName,
        String screenName,
        String screenType,
        LocalDateTime startsAt,
        List<String> seatNames
) {}
