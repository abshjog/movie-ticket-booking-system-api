package com.example.mdb.exception;

public class ScreeningOverlapException extends RuntimeException{

    private String message;

    public ScreeningOverlapException(String message) {
        this.message = message;
    }
}
