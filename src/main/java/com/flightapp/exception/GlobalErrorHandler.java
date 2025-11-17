package com.flightapp.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalErrorHandler {

    // 1) Validation errors (@Valid on @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();

        List<ObjectError> errorList = e.getBindingResult().getAllErrors();

        errorList.forEach(error -> {
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField();
                String message = fieldError.getDefaultMessage();
                errorMap.put(fieldName, message);
            } else {
                // global (object-level) errors – optional
                String message = error.getDefaultMessage();
                errorMap.put("globalError", message);
            }
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }

    // 2) Not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 3) Seat not available
    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<String> handleSeatNotAvailable(SeatNotAvailableException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // 4) Cancellation not allowed
    @ExceptionHandler(CancellationNotAllowedException.class)
    public ResponseEntity<String> handleCancellationNotAllowed(CancellationNotAllowedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // 5) Fallback for any other uncaught exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception e, HttpServletRequest request) {
        // you can log e.printStackTrace() or use a logger here
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage());
    }
}