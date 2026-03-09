package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.LoginDataResponseDTO;
import edu.cit.auditor.paluto.dto.LoginRequestDTO;
import edu.cit.auditor.paluto.entity.User;
import edu.cit.auditor.paluto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // Ensure you have a JwtService for tokens

    public LoginDataResponseDTO authenticate(LoginRequestDTO request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("AUTH-001: Invalid credentials"));

        // 2. Verify Password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("AUTH-001: Invalid credentials");
        }

        // 3. Generate Tokens
        String accessToken = jwtService.generateToken(user);

        // 4. Map to your specific Contract structure
        return LoginDataResponseDTO.builder()
                .user(LoginDataResponseDTO.UserResponse.builder()
                        .email(user.getEmail())
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .role(user.getRole())
                        .build())
                .accessToken(accessToken)
                .refreshToken(null)//for implementation next time
                .build();
    }


}