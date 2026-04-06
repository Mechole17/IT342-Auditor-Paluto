package edu.cit.auditor.paluto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceCreationDTO {
    private String title;
    private String description;
    private Integer servingSize;
    private String ingredientsList;
    private BigDecimal ingredientsCost;
    private Integer estPrepTime;
    private String imageUrl;
}
