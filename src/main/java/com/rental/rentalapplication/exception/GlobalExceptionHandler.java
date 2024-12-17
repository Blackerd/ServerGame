package com.rental.rentalapplication.exception;

import com.rental.rentalapplication.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(Error.UNCATEGORIZED_EXCEPTION.getMessage());
        apiResponse.setStatus("failed");
        apiResponse.setTimeStamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();

        Error error = Error.INVALID_KEY;

        try {
            error = Error.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .message(error.getMessage())
                .status("failed")
                .timeStamp(LocalDateTime.now())
                .violation(error.getViolation())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = CustomException.class)
    ResponseEntity<ApiResponse> handlingAppException(CustomException exception) {
        Error error = exception.getError();
        ApiResponse apiResponse = ApiResponse.builder()
                .message(error.getMessage())
                .status("failed")
                .timeStamp(LocalDateTime.now())
                .violation(error.getViolation())
                .build();
        return ResponseEntity.status(error.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        Error error = Error.UNAUTHORIZED;
        return ResponseEntity.status(error.getStatusCode())
                .body(ApiResponse.builder()
                        .message(error.getMessage())
                        .status("failed")
                        .timeStamp(LocalDateTime.now())
                        .build());
    }
}
