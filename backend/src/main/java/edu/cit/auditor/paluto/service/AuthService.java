package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.LoginDataResponseDTO;
import edu.cit.auditor.paluto.dto.LoginRequestDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.entity.Customer;
import edu.cit.auditor.paluto.entity.User;
import edu.cit.auditor.paluto.repository.CookRepository;
import edu.cit.auditor.paluto.repository.CustomerRepository;
import edu.cit.auditor.paluto.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // Ensure you have a JwtService for tokens
    private final CustomerRepository customerRepository;
    private final CookRepository cookRepository;

    public LoginDataResponseDTO authenticate(LoginRequestDTO request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 2. Verify Password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. Generate Tokens
        String accessToken = jwtService.generateToken(user);

        // 4. Map to your specific Contract structure
        return LoginDataResponseDTO.builder()
                .user(LoginDataResponseDTO.UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .role(user.getRole())
                        .build())
                .accessToken(accessToken)
                .refreshToken(null)//for implementation next time
                .build();
    }

    @Transactional
    public LoginDataResponseDTO registerOAuthFinal(Map<String, Object> data) {
        String role = (String) data.get("role");
        String email = (String) data.get("email");

        if ("COOK".equals(role)) {
            // Create a COOK object (which includes User fields)
            Cook newCook = Cook.builder()
                    .firstname((String) data.get("firstName"))
                    .lastname((String) data.get("lastName"))
                    .email(email)
                    .password("")
                    .address((String) data.get("address"))
                    .role("COOK")
                    .auth_provider("GOOGLE")
                    .created_at(LocalDateTime.now())

                    //cook professional details
                    .hourly_rate(Double.valueOf(data.get("hourly_rate").toString()))
                    .years_xp(Integer.valueOf(data.get("years_xp").toString()))
                    .bio((String) data.get("bio"))
                    .build();

            cookRepository.save(newCook); // Saves to BOTH user and cook tables
            return generateAuthResponse(newCook);

        } else {
            // Create a CUSTOMER object
            Customer newCustomer = Customer.builder()
                    .email(email)
                    .firstname((String) data.get("firstName"))
                    .lastname((String) data.get("lastName"))
                    .address((String) data.get("address"))
                    .role("CUSTOMER")
                    .auth_provider("GOOGLE")
                    .created_at(LocalDateTime.now())
                    .password("")
                    .build();

            customerRepository.save(newCustomer);
            return generateAuthResponse(newCustomer);
        }
    }

    private LoginDataResponseDTO generateAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return LoginDataResponseDTO.builder()
                .accessToken(token)
                .user(LoginDataResponseDTO.UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .role(user.getRole())
                        .build())
                .build();
    }
}