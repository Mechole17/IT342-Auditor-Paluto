package edu.cit.auditor.paluto.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Required for Postman POST requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/cook/register").permitAll() // Open the door for registration
                        .requestMatchers("/api/customer/register").permitAll() // Open the door for registration
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
