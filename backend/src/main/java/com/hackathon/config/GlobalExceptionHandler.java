package com.hackathon.config;

import com.hackathon.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation hatası");
        return ResponseEntity.badRequest().body(ApiResponse.fail(msg));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleFileTooLarge(MaxUploadSizeExceededException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail("Dosya çok büyük (maks 10MB)"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
        System.err.println("[ERROR] " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(500).body(ApiResponse.fail(ex.getMessage()));
    }
}
