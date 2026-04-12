package com.example.mdb.controller;

import com.example.mdb.dto.BookingRequest;
import com.example.mdb.dto.BookingResponse;
import com.example.mdb.service.BookingService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping("/movies/{movieId}/shows/{showId}/bookings")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseStructure<BookingResponse>> createBooking(
            @PathVariable String movieId,
            @PathVariable String showId,
            @Valid @RequestBody BookingRequest bookingRequest,
            Authentication auth) {

        String email = auth.getName();

        BookingResponse response = bookingService.createBooking(bookingRequest, showId, email);
        return responseBuilder.success(HttpStatus.CREATED, "Booking Initiated! Status: PENDING", response);
    }

    @PostMapping("/bookings/{bookingId}/confirm")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseStructure<BookingResponse>> confirmBooking(
            @PathVariable String bookingId) {

        BookingResponse response = bookingService.confirmBooking(bookingId);
        return responseBuilder.success(HttpStatus.OK, "Ticket Confirmed! Enjoy your movie 🍿", response);
    }
}