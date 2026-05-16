package com.example.mdb.mapper;

import com.example.mdb.dto.BookingResponse;
import com.example.mdb.entity.Booking;
import com.example.mdb.entity.Seat;
import com.example.mdb.utility.QRCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BookingMapper {

    private final QRCodeGenerator qrCodeGenerator;

    public BookingResponse mapToResponse(Booking booking) {
        if (booking == null) return null;

        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .referenceCode(booking.getReferenceCode())
                .razorpayOrderId(booking.getRazorpayOrderId())
                .baseAmount(booking.getBaseAmount())
                .taxAmount(booking.getTaxAmount())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getBookingStatus().name())
                .movieTitle(booking.getShow().getMovie().getTitle())
                .theaterName(booking.getShow().getScreen().getTheater().getName())
                .screenName(booking.getShow().getScreen().getName())
                .screenType(booking.getShow().getScreen().getScreenType().name())
                .startsAt(LocalDateTime.ofInstant(
                        booking.getShow().getStartsAt(),
                        ZoneId.of("Asia/Kolkata")
                ))
                .bookedAt(booking.getCreatedAt() != null ? LocalDateTime.ofInstant(
                        booking.getCreatedAt(),
                        ZoneId.of("Asia/Kolkata")
                ) : null)
                .seatNames(booking.getSeats().stream()
                        .map(Seat::getName)
                        .sorted()
                        .collect(Collectors.toList()))
                .razorpayRefundId(booking.getRazorpayRefundId())
                // Generating and setting Base64 string for UI
                .qrCodeBase64(qrCodeGenerator.generateQRCodeBase64(booking.getReferenceCode()))
                .build();
    }
}