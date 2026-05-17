package edu.cit.auditor.paluto.authentication;

import edu.cit.auditor.paluto.core.entities.Customer;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import edu.cit.auditor.paluto.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        mockCustomer = new Customer();
        mockCustomer.setEmail("test@example.com");
        mockCustomer.setPassword("encodedPassword");
        mockCustomer.setFirstname("John");
        mockCustomer.setLastname("Doe");
        mockCustomer.setRole("CUSTOMER");
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockCustomer));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);
        when(jwtService.generateToken(mockCustomer))
                .thenReturn("mock.jwt.token");

        LoginDataResponseDTO result = authService.authenticate(
                new LoginRequestDTO("test@example.com", "password123")
        );

        assertNotNull(result);
        assertEquals("mock.jwt.token", result.getAccessToken());
        assertEquals("test@example.com", result.getUser().getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void shouldFailWithWrongPassword() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockCustomer));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword"))
                .thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                authService.authenticate(
                        new LoginRequestDTO("test@example.com", "wrongpassword")
                )
        );

        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void shouldFailWithNonExistentEmail() {
        when(userRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                authService.authenticate(
                        new LoginRequestDTO("notfound@example.com", "password123")
                )
        );

        assertEquals("Invalid credentials", ex.getMessage());
    }
}