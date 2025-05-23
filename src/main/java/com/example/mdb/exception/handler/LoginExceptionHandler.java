package com.example.mdb.exception.handler;

import com.example.mdb.utility.ErrorStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class LoginExceptionHandler {

    private final RestResponseBuilder responseBuilder;

    @ExceptionHandler
    ResponseEntity<ErrorStructure<String>> handleUsernameNotFoundException(UsernameNotFoundException ex){
        return responseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage(),"No Username Found");
    }
}
