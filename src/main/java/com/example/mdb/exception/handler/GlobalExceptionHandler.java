package com.example.mdb.exception.handler;

import com.example.mdb.exception.BookingNotAllowedException;
import com.example.mdb.exception.EmailAlreadyExistsException;
import com.example.mdb.exception.InvalidShowTimeException;
import com.example.mdb.exception.SeatAlreadyBookedException;
import com.example.mdb.utility.ErrorStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final RestResponseBuilder restResponseBuilder;

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorStructure<String>> handleSeatAlreadyBooked(SeatAlreadyBookedException ex) {
        return restResponseBuilder.error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "The seats you are trying to book are no longer available. Please select different seats."
        );
    }

    @ExceptionHandler(InvalidShowTimeException.class)
    public ResponseEntity<ErrorStructure<String>> handleInvalidShowTime(InvalidShowTimeException ex) {
        return restResponseBuilder.error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Show creation failed: Past dates are not allowed."
        );
    }

    @ExceptionHandler(BookingNotAllowedException.class)
    public ResponseEntity<ErrorStructure<String>> handleBookingNotAllowed(BookingNotAllowedException ex) {
        return restResponseBuilder.error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Transaction Halted: Show time has already passed or status is invalid."
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorStructure<String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return restResponseBuilder.error(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                "A user with this email is already registered."
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorStructure<String>> handleIllegalStateException(IllegalStateException ex) {
        return restResponseBuilder.error(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                "Duplicate Review Not Allowed"
        );
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorStructure<String>> handleOptimisticLocking(OptimisticLockingFailureException ex) {
        return restResponseBuilder.error(
                HttpStatus.CONFLICT, // 409 Conflict
                "Transaction Conflict: Data was modified by another user.",
                "Oops! Someone else just grabbed those seats. Please refresh the seat map and try again."
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorStructure<String>> handleRuntimeException(RuntimeException ex) {
        return restResponseBuilder.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                "An unexpected runtime error occurred."
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
}
