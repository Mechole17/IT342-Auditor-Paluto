package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.CustomerRegistrationDTO;
import edu.cit.auditor.paluto.authentication.LoginDataResponseDTO;
import edu.cit.auditor.paluto.core.entities.Customer;
import edu.cit.auditor.paluto.infrastructure.exception.EmailAlreadyExistsException;
import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.service.CustomerService;
import edu.cit.auditor.paluto.service.JwtService;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

            return ResponseUtility.success(authData,HttpStatus.CREATED);
        }catch (EmailAlreadyExistsException e){
            return ResponseUtility.error("DB-002", e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
