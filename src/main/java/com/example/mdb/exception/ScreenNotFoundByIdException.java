package com.example.mdb.exception;

import lombok.Getter;

@Getter
public class ScreenNotFoundByIdException extends RuntimeException{

    private String message;

    public ScreenNotFoundByIdException(String message) {
        this.message = message;
    }
}
