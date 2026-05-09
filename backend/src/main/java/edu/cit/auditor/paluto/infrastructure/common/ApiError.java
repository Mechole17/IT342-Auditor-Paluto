package edu.cit.auditor.paluto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String code;
    private String message;
    private Object details;
}
