package edu.cit.auditor.paluto.users;

import edu.cit.auditor.paluto.core.entities.Customer;
import edu.cit.auditor.paluto.core.events.UserRegisteredEvent;
import edu.cit.auditor.paluto.infrastructure.exception.EmailAlreadyExistsException;
import edu.cit.auditor.paluto.core.repositories.CustomerRepository;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Customer registerCustomer(CustomerRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use.");
        }
        Customer newCustomer = Customer.builder()
                // USER attr
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .address(dto.getAddress())
                .role("CUSTOMER") // Follows your role naming convention
                .authProvider("LOCAL")
                .createdAt(LocalDateTime.now())
                .build();

        Customer savedCustomer = customerRepository.save(newCustomer);

        // FIXED: Publish the Observer notification event!
        // This instantly triggers the WelcomeEmailListener asynchronously in the background.
        eventPublisher.publishEvent(new UserRegisteredEvent(savedCustomer));
        System.out.println("Registration event successfully published for Customer: " + savedCustomer.getEmail());

        return savedCustomer;
    }
}