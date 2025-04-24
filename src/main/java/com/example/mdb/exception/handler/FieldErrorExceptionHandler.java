package com.example.mdb.exception.handler;

import com.example.mdb.utility.FieldErrorStructure;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedList;
import java.util.List;

@ControllerAdvice
public class FieldErrorExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        // Retrieve all validation errors.
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();

        // Mapping each ObjectError to a CustomFieldError.
        List<CustomFieldError> customFieldErrors = new LinkedList<>();
        for (ObjectError error : errors) {
            if (error instanceof FieldError fieldError) {
                customFieldErrors.add(CustomFieldError.builder()
                        .field(fieldError.getField())
                        .rejectedValue(String.valueOf(fieldError.getRejectedValue()))
                        .errorMessage(fieldError.getDefaultMessage())
                        .build());
            }
        }

        // Build the FieldErrorStructure object using its builder.
        FieldErrorStructure<List<CustomFieldError>> errorResponse =
                FieldErrorStructure.<List<CustomFieldError>>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage("Invalid Input")
                        .data(customFieldErrors)
                        .build();

        // Return it via ResponseEntity.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @Getter
    @Builder
    @ToString
    public static class CustomFieldError {
        private final String field;
        private final Object rejectedValue;
        private final String errorMessage;
    }
}


// create FieldErrorExceptionHandler extending ResponseEntityExceptionHandler,
// Override the methodhandleMethodArgumentNot Valid,
// get object errors from the exception object,
// Iterate over the object errors using for each and downcast each object to its child type "FieldError"
// Add it into a List outside loop.
// Now return Response.

//To wrap the list of CustomFieldError objects inside generic FieldErrorStructure so that our client sees the desired output.