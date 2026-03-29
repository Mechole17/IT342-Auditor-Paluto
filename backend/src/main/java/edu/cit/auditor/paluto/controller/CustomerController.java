package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.CustomerRegistrationDTO;
import edu.cit.auditor.paluto.dto.LoginDataResponseDTO;
import edu.cit.auditor.paluto.entity.Customer;
import edu.cit.auditor.paluto.exception.EmailAlreadyExistsException;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.CustomerService;
import edu.cit.auditor.paluto.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final JwtService jwtService; // Add this for token generation

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginDataResponseDTO>> registerCustomer(@Valid @RequestBody CustomerRegistrationDTO dto) {
        //used LoginDataResponseDTO to handle auto-login
        try {
            Customer registeredCustomer = customerService.registerCustomer(dto);

            // Generate token immediately for Auto-Login
            String token = jwtService.generateToken(registeredCustomer);

            // Wrap user and token together
            LoginDataResponseDTO authData = new LoginDataResponseDTO(registeredCustomer, token, null);

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
