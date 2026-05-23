package edu.cit.auditor.paluto.core.repositories;

import edu.cit.auditor.paluto.core.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByCookId(Long cookId);
    Optional<Rating> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.cook.id = :cookId")
    Double getAverageRatingByCookId(@Param("cookId") Long cookId);

    @Query("SELECT r.cook.id, AVG(r.rating) FROM Rating r GROUP BY r.cook.id")
    List<Object[]> getAllAverageRatings();
}