package com.example.mdb.service;

import com.example.mdb.dto.BookingRequest;
import com.example.mdb.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest bookingRequest, String email);

    void expireOldBookings();

    BookingResponse cancelBooking(String bookingId, String email);

    void resendTicket(String bookingId, String email);

    List<BookingResponse> getMyBookings(String email);
}