package edu.cit.auditor.paluto.utils;

import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseUtility {

    private ResponseUtility() {}

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(
                ApiResponse.<T>builder()
                        .success(true)
                        .data(data)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String code, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(
                ApiResponse.<T>builder()
                        .success(false)
                        .error(ApiError.builder()
                                .code(code)
                                .message(message)
                                .build())
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }
}
