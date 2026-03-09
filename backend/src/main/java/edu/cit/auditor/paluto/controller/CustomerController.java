package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.CustomerRegistrationDTO;
import edu.cit.auditor.paluto.entity.Customer;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.CustomerService;
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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Customer>> registerCustomer(@RequestBody CustomerRegistrationDTO dto) {
        Customer registeredCustomer = customerService.registerCustomer(dto);

        ApiResponse<Customer> response = ApiResponse.<Customer>builder()
                .success(true)
                .data(registeredCustomer)
                .error(null)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
