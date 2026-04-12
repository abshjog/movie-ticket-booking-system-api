package com.example.mdb.scheduler;

import com.example.mdb.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class BookingCleanupScheduler {
    private final BookingService bookingService;

    @Scheduled(fixedDelay = 120000)
    public void cleanupExpiredBookings() {
        log.info("Checking for expired bookings at: {}", LocalDateTime.now());

        bookingService.expireOldBookings();
    }
}