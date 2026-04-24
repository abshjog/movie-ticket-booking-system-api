package com.example.mdb.service.impl;

import com.example.mdb.dto.BookingRequest;
import com.example.mdb.dto.BookingResponse;
import com.example.mdb.entity.*;
import com.example.mdb.enums.BookingStatus;
import com.example.mdb.exception.BookingNotAllowedException;
import com.example.mdb.exception.SeatAlreadyBookedException;
import com.example.mdb.exception.UserNotFoundException;
import com.example.mdb.repository.*;
import com.example.mdb.service.BookingService;
import com.example.mdb.service.NotificationService;
import com.example.mdb.service.PaymentService;
import com.example.mdb.utility.IdGenerator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.mdb.mapper.BookingMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingMapper bookingMapper;
    private final PaymentService paymentService;
    private final IdGenerator idGenerator;
    private final NotificationService notificationService;

    private static final int BOOKING_BUFFER_MINUTES = 5;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, String email) {

        UserDetails userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        User user = (User) userDetails;

        String showId = bookingRequest.showId();
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        Instant bookingDeadline = show.getStartsAt().minus(BOOKING_BUFFER_MINUTES, ChronoUnit.MINUTES);
        if (Instant.now().isAfter(bookingDeadline)) {
            throw new BookingNotAllowedException("Booking Closed: Online bookings are only allowed up to "
                    + BOOKING_BUFFER_MINUTES + " minutes before the show starts.");
        }

        List<ShowSeat> selectedShowSeats = showSeatRepository.findByShowShowIdAndSeatSeatIdIn(showId, bookingRequest.seatIds());

        if (selectedShowSeats.size() != bookingRequest.seatIds().size()) {
            throw new RuntimeException("One or more seats are invalid for this show!");
        }

        boolean alreadyOccupied = selectedShowSeats.stream().anyMatch(ShowSeat::isBooked);
        if (alreadyOccupied) {
            throw new SeatAlreadyBookedException("Conflict: Seats already booked/blocked.");
        }

        selectedShowSeats.forEach(ss -> ss.setBooked(true));
        showSeatRepository.saveAll(selectedShowSeats);

        double ticketPrice = show.getTicketPrice();
        int seatCount = selectedShowSeats.size();

        BigDecimal baseAmount = BigDecimal.valueOf(ticketPrice)
                .multiply(BigDecimal.valueOf(seatCount))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = baseAmount.multiply(BigDecimal.valueOf(0.18))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = baseAmount.add(taxAmount)
                .setScale(2, RoundingMode.HALF_UP);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);

        List<Seat> sortedSeats = selectedShowSeats.stream()
                .map(ShowSeat::getSeat)
                .sorted(Comparator.comparing(Seat::getName))
                .toList();

        booking.setSeats(sortedSeats);

        booking.setBaseAmount(baseAmount.doubleValue());
        booking.setTaxAmount(taxAmount.doubleValue());
        booking.setTotalAmount(totalAmount.doubleValue());

        booking.setBookingStatus(BookingStatus.PENDING);

        String referenceCode;
        do {
            referenceCode = idGenerator.generateReferenceCode();
        } while (bookingRepository.existsByReferenceCode(referenceCode));
        booking.setReferenceCode(referenceCode);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking Processed with Taxes: ID {} | Ref: {} | Total ₹{}",
                savedBooking.getBookingId(), savedBooking.getReferenceCode(), totalAmount);

        return bookingMapper.mapToResponse(savedBooking);
    }

    @Override
    @Transactional
    public void expireOldBookings() {
        Instant cutoffTime = Instant.now().minus(10, ChronoUnit.MINUTES);
        List<Booking> expiredBookings = bookingRepository.findAllByBookingStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoffTime);

        if (expiredBookings.isEmpty()) return;

        expiredBookings.forEach(booking -> {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            String showId = booking.getShow().getShowId();
            List<String> seatIds = booking.getSeats().stream().map(Seat::getSeatId).toList();
            List<ShowSeat> showSeatsToRelease = showSeatRepository.findByShowShowIdAndSeatSeatIdIn(showId, seatIds);

            showSeatsToRelease.forEach(ss -> ss.setBooked(false));
            showSeatRepository.saveAll(showSeatsToRelease);
        });
        bookingRepository.saveAll(expiredBookings);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(String bookingId, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        if (!booking.getUser().getEmail().equals(email)) {
            throw new BookingNotAllowedException("Security Alert: You can only cancel your own bookings!");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new BookingNotAllowedException("Cancellation failed: Only confirmed bookings can be cancelled.");
        }

        Show show = booking.getShow();
        long minutesToStart = Duration.between(Instant.now(), show.getStartsAt()).toMinutes();

        if (minutesToStart < 60) {
            throw new BookingNotAllowedException("Too Late! Cancellation is only allowed up to 60 minutes before the show starts.");
        }

        double refundPercent = 0.0;
        if (minutesToStart >= 120) {
            refundPercent = 0.75;
        } else if (minutesToStart >= 60) {
            refundPercent = 0.50;
        }

        BigDecimal baseAmount = BigDecimal.valueOf(booking.getBaseAmount());
        BigDecimal taxAmount = BigDecimal.valueOf(booking.getTaxAmount());

        BigDecimal refundBase = baseAmount.multiply(BigDecimal.valueOf(refundPercent))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalRefundAmount = refundBase.add(taxAmount)
                .setScale(2, RoundingMode.HALF_UP);

        try {
            String refundId = paymentService.initiateRefund(booking.getRazorpayPaymentId(), totalRefundAmount.doubleValue());
            booking.setRazorpayRefundId(refundId);
        } catch (Exception e) {
            log.error("Payment Gateway Error during refund for Booking ID: {}. Reason: {}", bookingId, e.getMessage());
            throw new RuntimeException("Refund processing failed with Payment Gateway. Cancellation aborted.");
        }

        List<String> seatIds = booking.getSeats().stream()
                .map(Seat::getSeatId)
                .toList();

        List<ShowSeat> bookedSeats = showSeatRepository.findByShowShowIdAndSeatSeatIdIn(show.getShowId(), seatIds);

        bookedSeats.forEach(ss -> ss.setBooked(false));
        showSeatRepository.saveAll(bookedSeats);

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        log.info("Booking Cancelled successfully. ID: {} | User: {} | Refund Amount: ₹{}", bookingId, email, totalRefundAmount);

        return bookingMapper.mapToResponse(cancelledBooking);
    }

    @Override
    @Transactional
    public void resendTicket(String bookingId, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        if (!booking.getUser().getEmail().equals(email)) {
            throw new BookingNotAllowedException("Security Alert: You can only request tickets for your own bookings!");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new BookingNotAllowedException("Booking is not in CONFIRMED state.");
        }

        booking.getSeats().size();
        booking.getUser().getFullName();
        booking.getRazorpayOrderId();

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

        notificationService.sendBookingConfirmation(booking);
        log.info("Ticket resend triggered for Reference: {}", booking.getReferenceCode());
    }
}