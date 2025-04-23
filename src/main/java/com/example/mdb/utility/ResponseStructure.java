package com.example.mdb.utility;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
public class ResponseStructure<T> {

    private int statusCode;
    private String message;
    private T data;

    // API method for a successful response using builder
    public static <T> ResponseEntity<ResponseStructure<T>> success(HttpStatus status, String message, T data) {
        ResponseStructure<T> response = ResponseStructure.<T>builder()
                .data(data)
                .message(message)
                .statusCode(status.value())
                .build();
        return new ResponseEntity<>(response, status);
    }

    // API method for an error response using builder
    public static <T> ResponseEntity<ResponseStructure<T>> error(HttpStatus status, String message) {
        ResponseStructure<T> response = ResponseStructure.<T>builder()
                .data(null)
                .message(message)
                .statusCode(status.value())
                .build();
        return new ResponseEntity<>(response, status);
    }
}
