package com.example.mdb.exception;

public class TheaterNotFoundByIdException extends RuntimeException{
    public TheaterNotFoundByIdException(String message) {
        super(message);
    }
}
