package edu.cit.auditor.paluto.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceResponseDTO {
    private Long id;
    private Long cookId;
    private String title;
    private String description;
    private String ingredientsList;
    private BigDecimal ingredientsCost;
    private String imageUrl;
    private Integer estPrepTime;
    private Integer servingSize;

    // Add the missing piece here!
    @Column(precision = 19, scale = 2)
    private BigDecimal cookHourlyRate;
}