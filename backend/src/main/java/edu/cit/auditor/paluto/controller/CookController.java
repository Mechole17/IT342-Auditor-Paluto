package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.CookRegistrationDTO;
import edu.cit.auditor.paluto.dto.LoginDataResponseDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.exception.EmailAlreadyExistsException;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.CookService;
import edu.cit.auditor.paluto.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cook")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CookController {
    private final CookService cookService;
    private final JwtService jwtService; // Add this for token generation

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginDataResponseDTO>> registerCook(@Valid @RequestBody CookRegistrationDTO dto){
        //used LoginDataResponseDTO to handle auto-login
        try {
            Cook registeredCook = cookService.registerCook(dto);

            // Generate token immediately for Auto-Login
            String token = jwtService.generateToken(registeredCook);

            // Wrap user and token together
            LoginDataResponseDTO authData = new LoginDataResponseDTO(registeredCook, token, null);

            ApiResponse<LoginDataResponseDTO> response = ApiResponse.<LoginDataResponseDTO>builder()
                    .success(true)
                    .data(authData)
                    .error(null)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (EmailAlreadyExistsException e){
            ApiResponse<LoginDataResponseDTO> errorResponse = ApiResponse.<LoginDataResponseDTO>builder()
                    .success(false)
                    .data(null)
                    .error(ApiError.builder()
                            .code("DB-002")
                            .message(e.getMessage())
                            .build())
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
    }
}
