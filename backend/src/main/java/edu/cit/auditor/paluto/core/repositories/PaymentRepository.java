package edu.cit.auditor.paluto.core.repositories;

import edu.cit.auditor.paluto.core.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository <Payment, Long> {
}
