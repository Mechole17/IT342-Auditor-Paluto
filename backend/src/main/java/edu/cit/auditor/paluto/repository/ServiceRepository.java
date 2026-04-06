package edu.cit.auditor.paluto.repository;

import edu.cit.auditor.paluto.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByCookId(Long cookId);
}
