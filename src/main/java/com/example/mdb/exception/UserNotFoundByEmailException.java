package com.example.mdb.exception;

public class UserNotFoundByEmailException extends RuntimeException{
    public UserNotFoundByEmailException(String message) {
        super(message);
    }
}
