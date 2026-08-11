package com.grote.common;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final boolean error;
    private final T data;
    private final String message;

    private ApiResponse(boolean error, T data, String message) {
        this.error = error;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(false, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(true, null, message);
    }
}