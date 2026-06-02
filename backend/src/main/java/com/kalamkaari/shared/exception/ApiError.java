package com.kalamkaari.shared.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ApiError {
    private String message;
    private Map<String, String> errors;
}
