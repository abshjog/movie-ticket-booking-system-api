package com.example.mdb.controller;

import com.example.mdb.dto.BookingResponse;
import com.example.mdb.dto.PaymentCallbackRequest;
import com.example.mdb.service.PaymentService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping("/create-order/{bookingId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseStructure<Map<String, String>>> createPaymentOrder(@PathVariable String bookingId) {
        try {
            String razorpayOrderId = paymentService.createPaymentOrder(bookingId);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("razorpayOrderId", razorpayOrderId);

            return responseBuilder.success(HttpStatus.CREATED,
                    "Razorpay Order Created Successfully", responseData);

        } catch (Exception e) {
            throw new RuntimeException("Order creation failed: " + e.getMessage());
        }
    }

    @PostMapping("/verify/{bookingId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseStructure<BookingResponse>> verifyAndConfirm(
            @PathVariable String bookingId,
            @Valid @RequestBody PaymentCallbackRequest request) {

        try {
            BookingResponse response = paymentService.verifyPaymentAndConfirm(bookingId, request);

            return responseBuilder.success(HttpStatus.OK,
                    "Payment Verified & Ticket Confirmed! Enjoy your movie 🍿", response);

        } catch (Exception e) {
            throw new RuntimeException("Verification failed: " + e.getMessage());
        }
    }
}
