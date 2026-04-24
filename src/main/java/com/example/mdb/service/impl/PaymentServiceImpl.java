package com.example.mdb.service.impl;

import com.example.mdb.dto.BookingResponse;
import com.example.mdb.dto.PaymentCallbackRequest;
import com.example.mdb.entity.Booking;
import com.example.mdb.enums.BookingStatus;
import com.example.mdb.mapper.BookingMapper;
import com.example.mdb.repository.BookingRepository;
import com.example.mdb.service.NotificationService;
import com.example.mdb.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
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
    private final NotificationService notificationService;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    @Transactional
    public String createPaymentOrder(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        JSONObject orderRequest = new JSONObject();
        long amountInPaise = Math.round(booking.getTotalAmount() * 100);

        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + bookingId.substring(0, 8));

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        String razorpayOrderId = razorpayOrder.get("id");

        booking.setRazorpayOrderId(razorpayOrderId);
        bookingRepository.save(booking);

        log.info("Razorpay order created successfully. Amount: {} paise. Order ID: {}", amountInPaise, razorpayOrderId);
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
        //boolean isValid = true;

        if (isValid) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Verification failed: Booking record not found."));

            if (booking.getBookingStatus() == BookingStatus.EXPIRED) {
                log.warn("Payment verification attempted for an EXPIRED booking. ID: {}", bookingId);
                throw new RuntimeException("Transaction Timeout: The booking has expired.");
            }

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            booking.setRazorpayPaymentId(request.razorpayPaymentId());
            booking.setRazorpaySignature(request.razorpaySignature());

            booking.getUser().getFullName();
            booking.getSeats().size();
            if (booking.getShow() != null) {
                booking.getShow().getStartsAt();
                if (booking.getShow().getMovie() != null) booking.getShow().getMovie().getTitle();
                if (booking.getShow().getScreen() != null) {
                    booking.getShow().getScreen().getName();
                    booking.getShow().getScreen().getScreenType();
                    if (booking.getShow().getScreen().getTheater() != null) {
                        booking.getShow().getScreen().getTheater().getName();
                        booking.getShow().getScreen().getTheater().getAddress();
                        booking.getShow().getScreen().getTheater().getCity();
                    }
                }
            }

            Booking savedBooking = bookingRepository.save(booking);
            log.info("Payment verified and booking confirmed successfully. Booking ID: {}", bookingId);

            notificationService.sendBookingConfirmation(savedBooking);

            return bookingMapper.mapToResponse(savedBooking);
        } else {
            log.error("Signature mismatch detected for Booking ID: {}", bookingId);
            throw new RuntimeException("Payment verification failed: Invalid signature provided.");
        }
    }

    @Override
    @Transactional
    public String initiateRefund(String razorpayPaymentId, double refundAmount) throws Exception {
        if (razorpayPaymentId == null || razorpayPaymentId.isEmpty()) {
            throw new RuntimeException("Razorpay Payment ID is missing. Cannot process refund.");
        }

        JSONObject refundRequest = new JSONObject();
        long amountInPaise = Math.round(refundAmount * 100);
        refundRequest.put("amount", amountInPaise);
        refundRequest.put("speed", "optimum");

        Refund refund = razorpayClient.payments.refund(razorpayPaymentId, refundRequest);
        String refundId = refund.get("id");

        log.info("Razorpay refund initiated successfully. Refund ID: {} for Payment ID: {}", refundId, razorpayPaymentId);
        return refundId;
    }
}