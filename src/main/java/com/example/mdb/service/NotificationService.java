package com.example.mdb.service;

import com.example.mdb.entity.Booking;

public interface NotificationService {

    void sendBookingConfirmation(Booking booking);
}
