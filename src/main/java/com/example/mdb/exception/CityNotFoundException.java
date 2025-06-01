package com.example.mdb.exception;

import lombok.Getter;

@Getter
public class CityNotFoundException extends RuntimeException {

    private String message;

    public CityNotFoundException(String message) {
        this.message = message;
    }
}
