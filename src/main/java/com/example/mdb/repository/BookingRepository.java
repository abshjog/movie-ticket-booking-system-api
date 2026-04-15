package com.example.mdb.repository;

import com.example.mdb.entity.Booking;
import com.example.mdb.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findByRazorpayOrderId(String razorpayOrderId);

    @Query("SELECT COUNT(s) FROM Booking b JOIN b.seats s " +
            "WHERE b.show.showId = :showId " +
            "AND s.seatId IN :seatIds " +
            "AND b.bookingStatus IN (com.example.mdb.enums.BookingStatus.CONFIRMED, com.example.mdb.enums.BookingStatus.PENDING)")
    long countBookedSeats(@Param("showId") String showId, @Param("seatIds") List<String> seatIds);

    List<Booking> findAllByBookingStatusAndCreatedAtBefore(BookingStatus status, Instant instant);

    @Query("SELECT s.seatId FROM Booking b JOIN b.seats s " +
            "WHERE b.show.showId = :showId " +
            "AND b.bookingStatus IN (com.example.mdb.enums.BookingStatus.CONFIRMED, com.example.mdb.enums.BookingStatus.PENDING)")
    List<String> findBookedSeatIdsByShowId(@Param("showId") String showId);
}
