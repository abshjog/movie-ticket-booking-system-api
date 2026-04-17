package com.example.mdb.service;

import com.example.mdb.dto.BookingResponse;
import com.example.mdb.dto.PaymentCallbackRequest;

public interface PaymentService {

    String createPaymentOrder(String bookingId) throws Exception;

    BookingResponse verifyPaymentAndConfirm(String bookingId, PaymentCallbackRequest request) throws Exception;

    String initiateRefund(String razorpayPaymentId, double refundAmount) throws Exception;
}
