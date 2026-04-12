package edu.cit.auditor.paluto.repository;

import edu.cit.auditor.paluto.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // For Cook: "Show me my orders"
    List<Booking> findByCookIdOrderByCreatedAtDesc(Long cookId);
    List<Booking> findByCustomerId(Long customerId);
    // For Customer: "Show me what I ordered"
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    boolean existsByCookIdAndScheduledDateAndStatusIn(
            Long cookId,
            LocalDate scheduledDate,
            List<String> activeStatuses
    );
    @Query("SELECT b.scheduledDate FROM Booking b WHERE b.cook.id = :cookId AND b.status IN ('PAID_PENDING', 'ACCEPTED')")
    List<LocalDate> findBookedDatesByCookId(@Param("cookId") Long cookId);
}
