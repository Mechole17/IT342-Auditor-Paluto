package edu.cit.auditor.paluto.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String ingredientsList;
    private BigDecimal ingredientsCost;
    private String imageUrl;
    private Integer estPrepTime;
    private Integer servingSize;

    // Add the missing piece here!
    private Double cookHourlyRate;
}