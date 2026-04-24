package com.example.mdb.controller;

import com.example.mdb.dto.BookingRequest;
import com.example.mdb.dto.BookingResponse;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.service.BookingService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseStructure<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest bookingRequest,
            Authentication auth) {

        String email = auth.getName();
        BookingResponse response = bookingService.createBooking(bookingRequest, email);
        return responseBuilder.success(HttpStatus.CREATED, "Booking Initiated! Status: PENDING", response);
    }

    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseStructure<BookingResponse>> cancelBooking(
            @PathVariable String bookingId,
            Authentication auth) {

        String email = auth.getName();
        BookingResponse response = bookingService.cancelBooking(bookingId, email);
        return responseBuilder.success(HttpStatus.OK, "Booking Cancelled! Your seats are now available for others.", response);
    }

    @PostMapping("/{bookingId}/resend-ticket")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseStructure<String>> resendTicket(
            @PathVariable String bookingId,
            Authentication auth) {
        bookingService.resendTicket(bookingId, auth.getName());
        return ResponseStructure.success(HttpStatus.OK, "Ticket resent successfully to your registered email.", "Dispatched");
    }
}