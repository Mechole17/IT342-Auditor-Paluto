package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.CustomerRegistrationDTO;
import edu.cit.auditor.paluto.entity.Customer;
import edu.cit.auditor.paluto.entity.User;
import edu.cit.auditor.paluto.repository.CustomerRepository;
import edu.cit.auditor.paluto.repository.UserRepository;
import edu.cit.auditor.paluto.response.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(UserRepository userRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Customer registerCustomer(CustomerRegistrationDTO dto) {
        Customer newCustomer = Customer.builder()
                // USER attr
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .address(dto.getAddress())
                .role("CUSTOMER") // Follows your role naming convention
                .auth_provider("LOCAL")
                .created_at(LocalDateTime.now())
                .build();

        return customerRepository.save(newCustomer);
    }
}