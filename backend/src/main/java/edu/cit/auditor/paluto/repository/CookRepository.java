package edu.cit.auditor.paluto.repository;

import edu.cit.auditor.paluto.entity.Cook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookRepository extends JpaRepository<Cook, Long> {
    public boolean findByEmail(String email);
}
