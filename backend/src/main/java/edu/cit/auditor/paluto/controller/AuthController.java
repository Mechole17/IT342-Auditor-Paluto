package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.CookRegistrationDTO;
import edu.cit.auditor.paluto.dto.LoginDataResponseDTO;
import edu.cit.auditor.paluto.dto.LoginRequestDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.entity.User;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.AuthService;
import edu.cit.auditor.paluto.service.JwtService;
import edu.cit.auditor.paluto.utils.ResponseUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDataResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginDataResponseDTO data = authService.authenticate(request);

            return ResponseUtility.success(data,HttpStatus.OK);

        } catch (Exception e) {
            return ResponseUtility.error("AUTH-001", e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginDataResponseDTO>> getCurrentUser() {
        try {
            // The "Happy Path": Service finds user and returns DTO
            LoginDataResponseDTO response = authService.getCurrentUser();
            return ResponseUtility.success(response, HttpStatus.OK);
        } catch (Exception e) {
            // The "Safety Net": If token is invalid or user is missing
            // This prevents the CORS/Redirect loop and tells React what went wrong
            return ResponseUtility.error("DB-003","Session expired or user not found: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    //google auth endpoint
    @PostMapping("/register-oauth-final")
    public ResponseEntity<ApiResponse<LoginDataResponseDTO>> registerOAuthFinal(@RequestBody Map<String, Object> data) {
        // We pass the map to AuthService to handle the logic of splitting
        // data between User and Cook/Customer tables
        LoginDataResponseDTO response = authService.registerOAuthFinal(data);
        return ResponseUtility.success(response, HttpStatus.CREATED);
    }
}