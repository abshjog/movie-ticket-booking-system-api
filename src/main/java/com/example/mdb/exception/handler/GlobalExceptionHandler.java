package com.example.mdb.exception.handler;

import com.example.mdb.exception.EmailAlreadyExistsException;
import com.example.mdb.utility.ErrorStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ErrorStructure<String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ErrorStructure<String> errorResponse = ErrorStructure.<String>builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .error("A user with the given email already exists in the Database.")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
