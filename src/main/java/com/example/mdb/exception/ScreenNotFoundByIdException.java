package com.example.mdb.exception;

public class ScreenNotFoundByIdException extends RuntimeException{
    public ScreenNotFoundByIdException(String message) {
        super(message);
    }
}
