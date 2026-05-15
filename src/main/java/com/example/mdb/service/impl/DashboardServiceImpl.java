package com.example.mdb.service.impl;

import com.example.mdb.dto.DashboardStatsResponse;
import com.example.mdb.dto.RecentBookingResponse;
import com.example.mdb.entity.Booking;
import com.example.mdb.repository.BookingRepository;
import com.example.mdb.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BookingRepository bookingRepository;

    @Override
    public DashboardStatsResponse getOwnerDashboardStats(String ownerEmail) {

        // 1. Revenue Calculation (Only Base Amount, No GST)
        Double totalRevenue = bookingRepository.calculateTotalBaseRevenueByOwner(ownerEmail);

        // 2. Tickets Sold Calculation
        long totalTickets = bookingRepository.countTotalTicketsSoldByOwner(ownerEmail);

        // 3. Recent Bookings Mapping
        List<Booking> recentBookings = bookingRepository.findTopRecentBookingsByOwner(ownerEmail);

        // Returning ISO-8601 format via LocalDateTime
        List<RecentBookingResponse> bookingResponses = recentBookings.stream()
                .limit(5)
                .map(b -> RecentBookingResponse.builder()
                        .customerName(b.getUser().getFullName())
                        .movieTitle(b.getShow().getMovie().getTitle())
                        .theaterName(b.getShow().getTheater().getName())
                        .baseAmountPaid(b.getBaseAmount())
                        .bookedAt(b.getCreatedAt() != null ? LocalDateTime.ofInstant(
                                b.getCreatedAt(),
                                ZoneId.of("Asia/Kolkata")
                        ) : null)
                        .build())
                .collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .totalTicketsSold(totalTickets)
                .recentBookings(bookingResponses)
                .build();
    }
}