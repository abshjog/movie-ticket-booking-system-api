package com.example.mdb.exception;

public class InvalidShowTimeException extends RuntimeException {
    public InvalidShowTimeException(String message) {
        super(message);
    }
}
