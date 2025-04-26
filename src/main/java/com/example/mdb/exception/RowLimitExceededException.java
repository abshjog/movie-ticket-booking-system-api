package com.example.mdb.exception;

import lombok.Getter;

@Getter
public class RowLimitExceededException extends RuntimeException{

    private String message;

    public RowLimitExceededException(String message) {
        this.message = message;
    }
}
