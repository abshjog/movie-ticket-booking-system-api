package com.example.mdb.exception;

public class ScreenNotFoundByIdException extends RuntimeException{

    private String message;

    public ScreenNotFoundByIdException(String message) {
        this.message = message;
    }
}
