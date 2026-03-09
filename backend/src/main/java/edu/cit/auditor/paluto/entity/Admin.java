package edu.cit.auditor.paluto.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder // Required to work with User's SuperBuilder
public class Admin extends User {
    // Admins usually don't need extra fields like bio or address,
    // but you can add 'permission_level' here if needed.
}
