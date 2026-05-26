package edu.cit.auditor.paluto.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.entities.Customer;
import edu.cit.auditor.paluto.core.entities.User;
import edu.cit.auditor.paluto.core.repositories.CookRepository;
import edu.cit.auditor.paluto.core.repositories.CustomerRepository;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import edu.cit.auditor.paluto.infrastructure.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // Ensure you have a JwtService for tokens
    private final CustomerRepository customerRepository;
    private final CookRepository cookRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public LoginDataResponseDTO authenticateGoogle(GoogleLoginRequestDTO request) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(request.getIdToken());
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                return generateAuthResponse(userOptional.get());
            } else {
                // Return a partial response or a flag indicating registration is needed
                // For now, let's return the user info so the frontend can proceed to register
                return LoginDataResponseDTO.builder()
                        .user(LoginDataResponseDTO.UserResponse.builder()
                                .email(email)
                                .firstname(givenName)
                                .lastname(familyName)
                                .build())
                        .accessToken(null) // No token yet because they aren't in DB
                        .build();
            }
        } else {
            throw new RuntimeException("Invalid ID token");
        }
    }

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
                        .address(user.getAddress())
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
                    .authProvider("GOOGLE")
                    .createdAt(LocalDateTime.now())

                    //cook professional details
                    .hourlyRate(data.get("hourly_rate") != null
                            ? new BigDecimal(data.get("hourly_rate").toString())
                            : BigDecimal.ZERO)
                    .yearsXp(data.get("years_xp") != null
                            ? Integer.valueOf(data.get("years_xp").toString())
                            : 0)
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
                    .authProvider("GOOGLE")
                    .createdAt(LocalDateTime.now())
                    .password("")
                    .build();

            customerRepository.save(newCustomer);
            return generateAuthResponse(newCustomer);
        }
    }

    public LoginDataResponseDTO getCurrentUser() {
        // 1. Get the email from the SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Find user or throw custom exception
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User session not found"));

        // 3. Map to DTO (LoginDataResponseDTO.UserResponse)
        LoginDataResponseDTO.UserResponse userDTO = LoginDataResponseDTO.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .address(user.getAddress()) // Added so React has the address immediately
                .build();

        return LoginDataResponseDTO.builder()
                .user(userDTO)
                .accessToken(null) // Token is already stored in frontend
                .build();
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