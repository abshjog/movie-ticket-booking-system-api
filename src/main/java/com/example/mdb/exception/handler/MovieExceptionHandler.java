package com.example.mdb.exception.handler;

import com.example.mdb.exception.MovieNotFoundByIdException;
import com.example.mdb.utility.ErrorStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class MovieExceptionHandler {

    private final RestResponseBuilder responseBuilder;

    @ExceptionHandler
    public ResponseEntity<ErrorStructure<String>> handleMovieNotFoundByIdException(MovieNotFoundByIdException ex) {
        return responseBuilder.error(HttpStatus.BAD_REQUEST,  ex.getMessage(), "Movie with the provided ID is not found. Please verify the ID and try again.");
    }
}
