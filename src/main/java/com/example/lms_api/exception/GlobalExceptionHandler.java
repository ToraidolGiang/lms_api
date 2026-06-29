package com.example.lms_api.exception;

import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getDefaultMessage())
                .findFirst().orElse("Dữ liệu không hợp lệ");
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        ex.printStackTrace(); // IN RA CONSOLE ĐỂ DEBUG
        String errorMsg = ex.getMessage();
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            errorMsg += " | Cause: " + ex.getCause().getMessage();
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // ĐỔI SANG 500 ĐỂ KHÔNG BỊ NHẦM LÀ 404
                .body(Map.of("error", errorMsg != null ? errorMsg : ex.getClass().getName()));
    }
}
