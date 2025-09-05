package com.brightway.brightway_dropout.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple error response for API error handling
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
}
