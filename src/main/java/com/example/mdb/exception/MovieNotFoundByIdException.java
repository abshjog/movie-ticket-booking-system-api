package com.example.mdb.exception;

import lombok.Getter;

@Getter
public class MovieNotFoundByIdException extends RuntimeException{

    private String message;

    public MovieNotFoundByIdException(String message) {
        this.message = message;
    }
}
