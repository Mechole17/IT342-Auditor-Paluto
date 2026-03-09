package edu.cit.auditor.paluto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;
    private String timestamp;
}

@Data
@Builder
class ApiError{
    private String code;
    private String message;
    private Object details;
}