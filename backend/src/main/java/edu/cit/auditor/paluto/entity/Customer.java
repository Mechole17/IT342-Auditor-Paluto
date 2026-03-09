package edu.cit.auditor.paluto.entity;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
public class Customer extends User{
}
