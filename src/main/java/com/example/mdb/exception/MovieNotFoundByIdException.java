package com.example.mdb.exception;

public class MovieNotFoundByIdException extends RuntimeException {
    public MovieNotFoundByIdException(String message) {
        super(message);
    }
}
