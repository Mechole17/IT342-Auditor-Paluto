package edu.cit.auditor.paluto.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDataResponseDTO {
    private UserResponse user;
    private String accessToken;
    private String refreshToken;

    @Data
    @Builder
    public static class UserResponse {
        private String email;
        private String firstname;
        private String lastname;
        private String role;
    }
}
