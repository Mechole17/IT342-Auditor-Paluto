package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.LoginDataResponseDTO;
import edu.cit.auditor.paluto.dto.LoginRequestDTO;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDataResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginDataResponseDTO data = authService.authenticate(request);

            ApiResponse<LoginDataResponseDTO> response = ApiResponse.<LoginDataResponseDTO>builder()
                    .success(true)
                    .data(data)
                    .error(null)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<LoginDataResponseDTO> errorResponse = ApiResponse.<LoginDataResponseDTO>builder()
                    .success(false)
                    .data(null)
                    .error(ApiError.builder()
                            .code("AUTH-001")
                            .message(e.getMessage())
                            .build())
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}