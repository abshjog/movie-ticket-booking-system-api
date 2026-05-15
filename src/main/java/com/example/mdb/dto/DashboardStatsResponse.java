package com.example.mdb.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record DashboardStatsResponse(
        Double totalRevenue,
        long totalTicketsSold,
        List<RecentBookingResponse> recentBookings
) {}