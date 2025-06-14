package com.example.mdb.exception.handler;

import com.example.mdb.exception.CityNotFoundException;
import com.example.mdb.exception.TheaterNotFoundByIdException;
import com.example.mdb.utility.ErrorStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class TheaterExceptionHandler {

    private final RestResponseBuilder responseBuilder;

    @ExceptionHandler(TheaterNotFoundByIdException.class)
    public ResponseEntity<ErrorStructure<String>> handleTheaterNotFoundByIdException(TheaterNotFoundByIdException ex) {
        return responseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), "Theater with the requested ID not found");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorStructure<String>> handleCityNotFoundException(CityNotFoundException ex){
        return responseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), "Entered city not found.");
    }
}
