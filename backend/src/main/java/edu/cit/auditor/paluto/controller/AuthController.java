package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.LoginDataResponseDTO;
import edu.cit.auditor.paluto.dto.LoginRequestDTO;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.AuthService;
import edu.cit.auditor.paluto.utils.ResponseUtility;
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

            return ResponseUtility.success(data,HttpStatus.OK);

        } catch (Exception e) {
            return ResponseUtility.error("AUTH-001", e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}