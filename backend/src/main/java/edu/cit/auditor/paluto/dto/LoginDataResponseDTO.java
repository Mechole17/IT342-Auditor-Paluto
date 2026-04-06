package edu.cit.auditor.paluto.dto;

import edu.cit.auditor.paluto.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Added for JSON deserialization
@AllArgsConstructor // Added so @Builder doesn't conflict with your manual constructor
public class LoginDataResponseDTO {
    private UserResponse user;
    private String accessToken;
    private String refreshToken;

    public LoginDataResponseDTO(User userEntity, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.user = UserResponse.builder()
                .email(userEntity.getEmail())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .role(userEntity.getRole())
                .build();
    }

    @Data
    @Builder
    public static class UserResponse {
        private Long id;
        private String email;
        private String firstname;
        private String lastname;
        private String role;
    }
}
