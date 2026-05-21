package edu.cit.auditor.paluto.core.repositories;

import edu.cit.auditor.paluto.core.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository <Payment, Long> {
    Optional<Payment> findByTransactionReference(String paymentId);
    Optional<Payment> findByBookingId(Long bookingId);
}
