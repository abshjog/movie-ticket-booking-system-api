package com.example.mdb.service;

import com.example.mdb.dto.BookingRequest;
import com.example.mdb.dto.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(BookingRequest bookingRequest, String email);

    void expireOldBookings();

    BookingResponse cancelBooking(String bookingId, String email);
}