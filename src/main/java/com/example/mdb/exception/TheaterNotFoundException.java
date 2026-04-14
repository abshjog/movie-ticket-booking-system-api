package com.example.mdb.exception;

public class TheaterNotFoundException extends RuntimeException{
    public TheaterNotFoundException(String message) {
        super(message);
    }
}
