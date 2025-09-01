package com.brightway.brightway_dropout.util;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse {
    public Boolean success;
    public String message;
    public Object data;

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
