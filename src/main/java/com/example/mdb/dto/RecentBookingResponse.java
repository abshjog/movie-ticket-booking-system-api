package com.example.mdb.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record RecentBookingResponse(
        String customerName,
        String movieTitle,
        String theaterName,
        Double baseAmountPaid,
        LocalDateTime bookedAt
) {}