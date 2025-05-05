package com.example.mdb.exception.handler;

import com.example.mdb.exception.ScreeningOverlapException;
import com.example.mdb.utility.ErrorStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class ShowExceptionHandler {

    private final RestResponseBuilder responseBuilder;

    @ExceptionHandler
    public ResponseEntity<ErrorStructure<String>> handleScreeningOverlapException(ScreeningOverlapException ex) {
        return responseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Screening time conflict!! Please choose a different time slot.");
    }
}
