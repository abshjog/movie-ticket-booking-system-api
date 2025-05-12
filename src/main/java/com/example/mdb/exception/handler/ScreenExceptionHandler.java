package com.example.mdb.exception.handler;

import com.example.mdb.exception.RowLimitExceededException;
import com.example.mdb.exception.ScreenNotFoundByIdException;
import com.example.mdb.utility.ErrorStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ScreenExceptionHandler {

    private final RestResponseBuilder responseBuilder;

    @ExceptionHandler
    public ResponseEntity<ErrorStructure<String>> handleRowLimitExceededException(RowLimitExceededException ex) {
        return responseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Row limit exceeded. Please try again");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorStructure<String>> handleScreenNotFoundByIdException(ScreenNotFoundByIdException ex) {
        return responseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), "Screen with the entered ID not found");
    }
}
