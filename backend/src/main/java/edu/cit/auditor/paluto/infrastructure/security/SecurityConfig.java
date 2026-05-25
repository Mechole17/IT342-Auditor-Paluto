package edu.cit.auditor.paluto.infrastructure.security;

import edu.cit.auditor.paluto.core.entities.User;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // Required for Postman POST requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,"/api/auth/me").authenticated()
                        .requestMatchers("/api/auth/**", "/api/cook/register", "/api/customer/register").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/services/all", "/api/services/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/services/**").hasAuthority("COOK")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/cooks/*/booked-dates").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cook/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/services/cook/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cook/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/customer/**").hasAuthority("CUSTOMER")
                        .requestMatchers("/api/services/create").hasAuthority("COOK") // Lock it down
                        .requestMatchers("/api/bookings/create").hasAuthority("CUSTOMER")
                        .requestMatchers("/api/storage/service-upload").hasAuthority("COOK") // ADD THIS
                        .requestMatchers("/api/payment/webhook").permitAll()
                        .requestMatchers("/api/payment/checkout").hasAuthority("CUSTOMER")
                        .requestMatchers(HttpMethod.GET,"api/admin/user").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/admin/dashboard-stats").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/cook/**").hasAuthority("COOK")
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/status").hasAuthority("COOK")
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/cancel-booking").hasAuthority("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/{id}").hasAnyAuthority("COOK", "CUSTOMER")

                        .requestMatchers(HttpMethod.GET, "/api/certificates/cook/**").permitAll()
                        .requestMatchers("/api/certificates/upload").hasAuthority("COOK")
                        .requestMatchers("/api/certificates/my-certificates").hasAuthority("COOK")
                        .requestMatchers(HttpMethod.DELETE, "/api/certificates/**").hasAuthority("COOK")
                        .requestMatchers("/api/certificates/all").hasAuthority("ADMIN")
                        .requestMatchers("/api/storage/certificate-upload").hasAuthority("COOK")
                        .requestMatchers("/api/certificates/pending").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/api/certificates/*/review").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/ratings/cook/**").permitAll()
                        .requestMatchers("/api/ratings/submit").hasAuthority("CUSTOMER")
                        .requestMatchers("/api/ratings/check/**").hasAuthority("CUSTOMER")
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // CRITICAL LINE
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                );
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            // Google uses these specific keys internally
            String googleEmail = oAuth2User.getAttribute("email");
            String googleFirstName = oAuth2User.getAttribute("given_name");
            String googleLastName = oAuth2User.getAttribute("family_name");

            Optional<User> userOptional = userRepository.findByEmail(googleEmail);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // THIS IS THE ONLY PLACE THAT RUNS DURING LOGIN
                if (!"GOOGLE".equalsIgnoreCase(user.getAuthProvider())) {
                    user.setAuthProvider("GOOGLE");
                    userRepository.save(user); // Now it will actually save to the DB!
                }
                String token = jwtService.generateToken(userOptional.get());
                response.sendRedirect("http://localhost:3000/oauth-success?token=" + token);
            } else {
                // Standardizing the URL parameters for React
                response.sendRedirect(String.format(
                        "http://localhost:3000/select-role?email=%s&firstName=%s&lastName=%s",
                        googleEmail, googleFirstName, googleLastName
                ));
            }
        };
    }
}
