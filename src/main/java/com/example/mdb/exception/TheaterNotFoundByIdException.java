package com.example.mdb.exception;

public class TheaterNotFoundByIdException extends RuntimeException{

    private String message;

    public TheaterNotFoundByIdException(String message) {
        this.message = message;
    }
}
