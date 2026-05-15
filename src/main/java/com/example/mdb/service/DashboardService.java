package com.example.mdb.service;

import com.example.mdb.dto.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getOwnerDashboardStats(String ownerEmail);
}
