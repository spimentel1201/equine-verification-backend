package com.horsetrust.common.web;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String code,
        Map<String, List<String>> fieldErrors
) {
    public static ApiError of(int status, String error, String message, String path, String code) {
        return new ApiError(Instant.now(), status, error, message, path, code, null);
    }

    public static ApiError validation(int status, String error, String message, String path, String code,
                                      Map<String, List<String>> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, path, code, fieldErrors);
    }
}