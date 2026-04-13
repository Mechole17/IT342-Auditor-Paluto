package edu.cit.auditor.paluto.security;

import edu.cit.auditor.paluto.entity.User;
import edu.cit.auditor.paluto.repository.UserRepository;
import edu.cit.auditor.paluto.service.AuthService;
import edu.cit.auditor.paluto.service.JwtService;
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
                        .requestMatchers(HttpMethod.GET, "/api/bookings/cooks/*/booked-dates").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/customer/**").hasAuthority("CUSTOMER")
                        .requestMatchers("/api/services/create").hasAuthority("COOK") // Lock it down
                        .requestMatchers("/api/bookings/create").hasAuthority("CUSTOMER")
                        .requestMatchers("/api/customer/payment/checkout").permitAll()//for testing purposes
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
                if (!"GOOGLE".equalsIgnoreCase(user.getAuth_provider())) {
                    user.setAuth_provider("GOOGLE");
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
