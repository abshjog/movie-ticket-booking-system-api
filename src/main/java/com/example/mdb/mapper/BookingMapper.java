package com.example.mdb.mapper;

import com.example.mdb.dto.BookingResponse;
import com.example.mdb.entity.Booking;
import com.example.mdb.entity.Seat;
import com.example.mdb.enums.SeatCategory;
import com.example.mdb.utility.QRCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BookingMapper {

    private final QRCodeGenerator qrCodeGenerator;

    private String getSeatPrefix(SeatCategory category) {
        if (category == null) return "";
        return switch (category) {
            case VIP -> "VIP - ";
            case PREMIUM -> "PR - ";
            case EXECUTIVE -> "EX - ";
            case NORMAL -> "NR - ";
        };
    }

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
                .theaterName(booking.getShow().getScreen().getTheater().getName())
                .address(booking.getShow().getScreen().getTheater().getAddress())
                .landmark(booking.getShow().getScreen().getTheater().getLandmark())
                .city(booking.getShow().getScreen().getTheater().getCity())
                .movieTitle(booking.getShow().getMovie().getTitle())
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
                        .collect(Collectors.groupingBy(Seat::getSeatCategory, TreeMap::new, Collectors.toList()))
                        .entrySet().stream()
                        .map(entry -> getSeatPrefix(entry.getKey()) + entry.getValue().stream()
                                .map(Seat::getName)
                                .collect(Collectors.joining(", ")))
                        .collect(Collectors.toList()))
                .razorpayRefundId(booking.getRazorpayRefundId())

                .qrCodeBase64(qrCodeGenerator.generateQRCodeBase64(booking.getReferenceCode()))
                .build();
    }
}