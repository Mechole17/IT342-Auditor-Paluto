package edu.cit.auditor.paluto.entity;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Cook extends User {
    private Double hourly_rate;
    private Integer years_xp;
    private String bio;

}
