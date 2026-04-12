package com.example.mdb.service.impl;

import com.example.mdb.dto.BookingRequest;
import com.example.mdb.dto.BookingResponse;
import com.example.mdb.entity.*;
import com.example.mdb.enums.BookingStatus;
import com.example.mdb.exception.BookingNotAllowedException;
import com.example.mdb.exception.SeatAlreadyBookedException;
import com.example.mdb.repository.*;
import com.example.mdb.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.mdb.mapper.BookingMapper;

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

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, String showId, String email) {

        UserDetails userDetails = userRepository.findByEmail(email);
        if (userDetails == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        User user = (User) userDetails;

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        if (show.getStartsAt().isBefore(Instant.now())) {
            throw new BookingNotAllowedException("Booking denied: Show already started or passed.");
        }

        List<ShowSeat> selectedShowSeats = showSeatRepository.findByShowShowIdAndSeatSeatIdIn(showId, bookingRequest.seatIds());

        if (selectedShowSeats.size() != bookingRequest.seatIds().size()) {
            throw new RuntimeException("One or more seats are invalid for this show!");
        }

        String currentScreenId = show.getScreen().getScreenId();
        boolean isValidScreen = selectedShowSeats.stream()
                .allMatch(ss -> ss.getSeat().getScreen().getScreenId().equals(currentScreenId));

        if (!isValidScreen) {
            throw new RuntimeException("Security Breach: Seats do not belong to the correct screen!");
        }

        boolean alreadyOccupied = selectedShowSeats.stream().anyMatch(ShowSeat::isBooked);
        if (alreadyOccupied) {
            throw new SeatAlreadyBookedException("Conflict: Seats already booked/blocked.");
        }

        selectedShowSeats.forEach(ss -> ss.setBooked(true));
        showSeatRepository.saveAll(selectedShowSeats);

        double totalAmount = selectedShowSeats.size() * show.getTicketPrice();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);

        List<Seat> sortedSeats = selectedShowSeats.stream()
                .map(ShowSeat::getSeat)
                .sorted(Comparator.comparing(Seat::getName))
                .toList();

        booking.setSeats(sortedSeats);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking Processed: ID {} | User {} | Show {}", savedBooking.getBookingId(), email, showId);

        return bookingMapper.mapToResponse(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponse confirmBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new BookingNotAllowedException("Invalid Status: Cannot confirm booking in " + booking.getBookingStatus() + " state.");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.mapToResponse(updatedBooking);
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
}
