package com.example.mdb.exception;

import lombok.Getter;

@Getter
public class EmailAlreadyExistsException extends RuntimeException {

    private String message;

    public EmailAlreadyExistsException(String message) {
        this.message = message;
    }
}
