package edu.cit.auditor.paluto.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Cook extends User {
    @Column(precision = 19, scale = 2)
    private BigDecimal hourlyRate;
    private Integer yearsXp;
    private String bio;

    @Builder.Default
    @Column(name = "earnings_balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal earningsBalance = BigDecimal.ZERO;

}
