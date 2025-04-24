package com.example.mdb.utility;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RestResponseBuilder {

    // API method for a successful response
    public <T> ResponseEntity<ResponseStructure<T>> success(HttpStatus status, String message, T data) {
        ResponseStructure<T> responseStructure = ResponseStructure
                .<T>builder()
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.status(status).body(responseStructure);
    }

    // API method for an error response
    public <T> ResponseEntity<ErrorStructure<T>> error(HttpStatus errorCode, String errorMessage) {
        ErrorStructure<T> errorStructure = ErrorStructure
                .<T>builder()
                .errorCode(errorCode.value())
                .message(errorMessage)
                .build();
        return ResponseEntity.status(errorCode).body(errorStructure);
    }
}
