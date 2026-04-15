package com.example.mdb.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentCallbackRequest(
        @NotBlank(message = "Order ID missing")
        String razorpayOrderId,
        @NotBlank(message = "Payment ID missing")
        String razorpayPaymentId,
        @NotBlank(message = "Signature missing")
        String razorpaySignature
) {}
