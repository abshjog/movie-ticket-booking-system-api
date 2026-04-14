package com.example.mdb.exception.handler;

import com.example.mdb.exception.*;
import com.example.mdb.utility.ErrorStructure;
import com.example.mdb.utility.FieldErrorStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedList;
import java.util.List;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final RestResponseBuilder restResponseBuilder;

    // 1. INPUT VALIDATION LOGIC (From FieldErrorExceptionHandler)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
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

        FieldErrorStructure<List<CustomFieldError>> errorResponse = FieldErrorStructure.<List<CustomFieldError>>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage("Invalid Input")
                .data(customFieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    // ========================================================================
    // 2. YOUR ORIGINAL LOGIC & MESSAGES
    // ========================================================================
    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorStructure<String>> handleSeatAlreadyBooked(SeatAlreadyBookedException ex) {
        return restResponseBuilder.error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "The seats you are trying to book are no longer available. Please select different seats."
        );
    }

    @ExceptionHandler(BookingNotAllowedException.class)
    public ResponseEntity<ErrorStructure<String>> handleBookingNotAllowed(BookingNotAllowedException ex) {
        return restResponseBuilder.error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Transaction Halted: Online booking for this show is now closed (Buffer: 5 mins). Please check other shows or visit the counter."
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorStructure<String>> handleAccessDenied(AccessDeniedException ex) {
        return restResponseBuilder.error(
                HttpStatus.FORBIDDEN,
                "Unauthorized Access: You do not have permission to perform this action.",
                "You do not have the required role/authority to access this resource."
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorStructure<String>> handleUsernameNotFound(UsernameNotFoundException ex) {
        return restResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage(), "No Username Found");
    }

    // ========================================================================
    // 3. MERGED RESOURCE NOT FOUND LOGIC (Movie, Theater, User, Screen)
    // ========================================================================
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorStructure<String>> handleUserNotFound(UserNotFoundException ex) {
        return restResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), "User or Owner not found in the Database.");
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorStructure<String>> handleMovieNotFound(MovieNotFoundException ex) {
        return restResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), "Movie with the provided ID is not found. Please verify the ID and try again.");
    }

    @ExceptionHandler(TheaterNotFoundException.class)
    public ResponseEntity<ErrorStructure<String>> handleTheaterNotFound(TheaterNotFoundException ex) {
        return restResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), "Theater with the requested ID not found");
    }

    @ExceptionHandler(ScreenNotFoundException.class)
    public ResponseEntity<ErrorStructure<String>> handleScreenNotFound(ScreenNotFoundException ex) {
        return restResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), "Screen with the entered ID not found");
    }

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ErrorStructure<String>> handleCityNotFound(CityNotFoundException ex) {
        return restResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), "Entered city not found.");
    }

    // ========================================================================
    // 4. BUSINESS CONFLICTS & RUNTIME LOGIC
    // ========================================================================
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorStructure<String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return restResponseBuilder.error(HttpStatus.CONFLICT, ex.getMessage(), "A user with this email is already registered.");
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorStructure<String>> handleOptimisticLocking(OptimisticLockingFailureException ex) {
        return restResponseBuilder.error(
                HttpStatus.CONFLICT,
                "Transaction Conflict: Data was modified by another user.",
                "Oops! Someone else just grabbed those seats. Please refresh the seat map and try again."
        );
    }

    @ExceptionHandler(ScreeningOverlapException.class)
    public ResponseEntity<ErrorStructure<String>> handleScreeningOverlap(ScreeningOverlapException ex) {
        return restResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Screening time conflict!! Please choose a different time slot.");
    }

    @ExceptionHandler(RowLimitExceededException.class)
    public ResponseEntity<ErrorStructure<String>> handleRowLimitExceeded(RowLimitExceededException ex) {
        return restResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Row limit exceeded. Please try again");
    }

    @ExceptionHandler(InvalidShowTimeException.class)
    public ResponseEntity<ErrorStructure<String>> handleInvalidShowTime(InvalidShowTimeException ex) {
        return restResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Show creation failed: Past dates are not allowed.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorStructure<String>> handleRuntimeException(RuntimeException ex) {
        return restResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "An unexpected runtime error occurred.");
    }

    // Helper DTO for validation errors
    @Getter
    @Builder
    @ToString
    public static class CustomFieldError {
        private final String field;
        private final Object rejectedValue;
        private final String errorMessage;
    }
}
