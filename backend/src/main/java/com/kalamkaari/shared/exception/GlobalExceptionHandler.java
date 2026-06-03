package com.kalamkaari.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ApiError.builder()
                .message("Validation failed")
                .errors(errors)
                .build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleProductNotFound(ProductNotFoundException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .errors(Collections.emptyMap())
                .build();
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFound(CategoryNotFoundException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .errors(Collections.emptyMap())
                .build();
    }

    @ExceptionHandler(CategoryInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCategoryInUse(CategoryInUseException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .errors(Collections.emptyMap())
                .build();
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleInsufficientStock(InsufficientStockException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .errors(Collections.emptyMap())
                .build();
    }
}
