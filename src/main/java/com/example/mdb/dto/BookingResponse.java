package com.example.mdb.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
@Builder
public record BookingResponse(
        String bookingId,
        String referenceCode,
        String razorpayOrderId,
        double baseAmount,
        double taxAmount,
        double totalAmount,
        String status,
        String movieTitle,
        String theaterName,
        String screenName,
        String screenType,
        LocalDateTime startsAt,
        LocalDateTime bookedAt,
        List<String> seatNames,
        String razorpayRefundId,
        String qrCodeBase64
) {}
