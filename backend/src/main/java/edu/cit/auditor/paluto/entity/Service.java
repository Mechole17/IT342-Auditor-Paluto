package edu.cit.auditor.paluto.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cook_id", nullable = false)
    @JsonIgnore
    private Cook cook;

    private String title;
    private String description;
    @Column(name = "serving_size")
    private Integer servingSize;
    @Column(name = "ingredients_ls")
    private String ingredientsList;
    @Column(name = "est_prep_time")
    private Integer estPrepTime;

    @Column(name="ingredients_cost",precision = 10, scale = 2)
    private BigDecimal ingredientsCost;

    @Column(name = "image_url")
    private String imageUrl;

    @Transient // Not in DB, but shows in JSON
    private Double cookHourlyRate;
}
