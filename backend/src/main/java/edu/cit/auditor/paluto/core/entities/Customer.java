package edu.cit.auditor.paluto.core.entities;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@SuperBuilder
public class Customer extends User{
}
