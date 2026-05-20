package edu.cit.auditor.paluto.core.repositories;

import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByCook(Cook cook);
    @Query("SELECT s FROM Service s JOIN FETCH s.cook WHERE s.id = :id")
    Optional<Service> findByIdWithCook(@Param("id") Long id);
}
