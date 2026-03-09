package edu.cit.auditor.paluto.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CookRegistrationDTO {
    //User attr
    @NotBlank(message = "First name is required")
    private String firstname;
    @NotBlank(message = "Last name is required")
    private String lastname;
    @NotBlank(message = "Address name is required")
    private String address;
    @Email
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and include uppercase, lowercase, a number, and a special character"
    )
    private String password;
    private String role;

    private String auth_provider;
    private LocalDateTime created_at;

    //Cook attr

    @NotNull(message = "Hourly rate is required")
    @Min(value = 0, message = "Hourly rate cannot be negative")
    private Double hourly_rate;

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer years_xp;
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;


}