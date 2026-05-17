package edu.cit.auditor.paluto.users;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CookResponseDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private BigDecimal hourlyRate;
    private Integer yearsXp;
    private String bio;
}
