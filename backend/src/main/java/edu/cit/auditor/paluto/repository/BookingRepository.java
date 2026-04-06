package edu.cit.auditor.paluto.repository;

import edu.cit.auditor.paluto.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // For Cook: "Show me my orders"
    List<Booking> findByCookIdOrderByCreatedAtDesc(Long cookId);

    // For Customer: "Show me what I ordered"
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
