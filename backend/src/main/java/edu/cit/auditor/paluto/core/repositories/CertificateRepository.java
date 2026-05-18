package edu.cit.auditor.paluto.core.repositories;

import edu.cit.auditor.paluto.core.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByCookId(Long cookId);
    List<Certificate> findByStatus(String status); // for admin to see all pending
    List<Certificate> findByCookIdAndStatusNot(Long cookId, String status);
    List<Certificate> findByStatusNot(String status);
}
