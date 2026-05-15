package com.example.mdb.controller;

import com.example.mdb.dto.DashboardStatsResponse;
import com.example.mdb.service.DashboardService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final RestResponseBuilder responseBuilder;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<DashboardStatsResponse>> getDashboardStats(Authentication auth) {
        String email = auth.getName();
        DashboardStatsResponse stats = dashboardService.getOwnerDashboardStats(email);
        return responseBuilder.success(HttpStatus.OK, "Theater Owner dashboard stats fetched successfully", stats);
    }
}