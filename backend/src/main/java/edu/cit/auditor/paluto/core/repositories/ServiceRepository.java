package edu.cit.auditor.paluto.core.repositories;

import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByCook(Cook cook);

}
