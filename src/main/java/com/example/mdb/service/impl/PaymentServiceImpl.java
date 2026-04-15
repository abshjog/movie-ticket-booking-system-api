package com.example.mdb.service.impl;

import com.example.mdb.dto.BookingResponse;
import com.example.mdb.dto.PaymentCallbackRequest;
import com.example.mdb.entity.Booking;
import com.example.mdb.enums.BookingStatus;
import com.example.mdb.mapper.BookingMapper;
import com.example.mdb.repository.BookingRepository;
import com.example.mdb.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    @Transactional
    public String createPaymentOrder(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        JSONObject orderRequest = new JSONObject();
        // Amount is converted to paise (1 INR = 100 Paise)
        orderRequest.put("amount", (int)(booking.getTotalAmount() * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + bookingId.substring(0, 8));

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        String razorpayOrderId = razorpayOrder.get("id");

        booking.setRazorpayOrderId(razorpayOrderId);
        bookingRepository.save(booking);

        log.info("Razorpay order successfully created. Order ID: {} for Booking ID: {}", razorpayOrderId, bookingId);
        return razorpayOrderId;
    }

    @Override
    @Transactional
    public BookingResponse verifyPaymentAndConfirm(String bookingId, PaymentCallbackRequest request) throws Exception {

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", request.razorpayOrderId());
        options.put("razorpay_payment_id", request.razorpayPaymentId());
        options.put("razorpay_signature", request.razorpaySignature());

        boolean isValid = Utils.verifyPaymentSignature(options, keySecret);
        // boolean isValid = true;

        if (isValid) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Verification failed: Booking record not found."));

            if (booking.getBookingStatus() == BookingStatus.EXPIRED) {
                log.warn("Payment verification attempted for an EXPIRED booking. ID: {}", bookingId);
                throw new RuntimeException("Transaction Timeout: The booking has expired (10-minute window exceeded). Refund will be initiated if amount was debited.");
            }

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            booking.setRazorpayPaymentId(request.razorpayPaymentId());
            booking.setRazorpaySignature(request.razorpaySignature());

            Booking savedBooking = bookingRepository.save(booking);
            log.info("Payment verified and booking confirmed successfully. Booking ID: {}", bookingId);

            return bookingMapper.mapToResponse(savedBooking);
        } else {
            log.error("Signature mismatch detected. Potential security breach or invalid data for Booking ID: {}", bookingId);
            throw new RuntimeException("Payment verification failed: Invalid signature provided.");
        }
    }
}
