package com.example.mdb.controller;

import com.example.mdb.dto.BookingRequest;
import com.example.mdb.dto.BookingResponse;
import com.example.mdb.service.BookingService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest bookingRequest,
            Authentication auth) {

        String email = auth.getName();
        BookingResponse response = bookingService.createBooking(bookingRequest, email);
        return responseBuilder.success(HttpStatus.CREATED, "Booking Initiated! Status: PENDING", response);
    }

    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAnyAuthority('USER', 'THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<BookingResponse>> cancelBooking(
            @PathVariable String bookingId,
            Authentication auth) {

        String email = auth.getName();
        BookingResponse response = bookingService.cancelBooking(bookingId, email);
        return responseBuilder.success(HttpStatus.OK, "Booking Cancelled! Your seats are now available for others.", response);
    }

    @PostMapping("/{bookingId}/resend-ticket")
    @PreAuthorize("hasAnyAuthority('USER', 'THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<String>> resendTicket(
            @PathVariable String bookingId,
            Authentication auth) {
        bookingService.resendTicket(bookingId, auth.getName());
        return ResponseStructure.success(HttpStatus.OK, "Ticket resent successfully to your registered email.", "Dispatched");
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyAuthority('USER', 'THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<List<BookingResponse>>> getMyBookings(Authentication auth) {
        String email = auth.getName();
        List<BookingResponse> responses = bookingService.getMyBookings(email);
        return responseBuilder.success(HttpStatus.OK, "Booking history fetched successfully", responses);
    }

    @GetMapping(value = "/{bookingId}/download-ticket", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'THEATER_OWNER')")
    public ResponseEntity<byte[]> downloadTicket(
            @PathVariable String bookingId,
            @RequestParam(name = "ref", defaultValue = "Ticket") String referenceCode,
            Authentication auth) {

        byte[] pdfBytes = bookingService.generateTicketPdf(bookingId, auth.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData("attachment", referenceCode + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}