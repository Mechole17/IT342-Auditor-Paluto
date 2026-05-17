package edu.cit.auditor.paluto.core.repositories;

import edu.cit.auditor.paluto.core.entities.Cook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CookRepository extends JpaRepository<Cook, Long> {
    Optional<Cook> findByEmail(String email);
}
