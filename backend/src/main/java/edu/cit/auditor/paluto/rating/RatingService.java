package edu.cit.auditor.paluto.rating;

import edu.cit.auditor.paluto.core.entities.*;
import edu.cit.auditor.paluto.core.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public RatingResponseDTO submitRating(String customerEmail, RatingRequestDTO dto) {
        User user = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        // Validate ownership
        if (!booking.getCustomer().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized — this is not your booking.");
        }

        // Validate booking is completed
        if (!"COMPLETED".equals(booking.getStatus())) {
            throw new RuntimeException("Can only rate completed bookings.");
        }

        // Validate one rating per booking
        if (ratingRepository.existsByBookingId(dto.getBookingId())) {
            throw new RuntimeException("You have already rated this booking.");
        }

        Rating rating = Rating.builder()
                .booking(booking)
                .cook(booking.getCook())
                .customer((Customer) user)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        Rating saved = ratingRepository.save(rating);
        return mapToDTO(saved);
    }

    public List<RatingResponseDTO> getCookRatings(Long cookId) {
        return ratingRepository.findByCookId(cookId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Double getCookAverageRating(Long cookId) {
        Double avg = ratingRepository.getAverageRatingByCookId(cookId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    public boolean hasRated(Long bookingId) {
        return ratingRepository.existsByBookingId(bookingId);
    }

    private RatingResponseDTO mapToDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .bookingId(rating.getBooking().getId())
                .cookId(rating.getCook().getId())
                .customerName(rating.getCustomer().getFirstname() + " " + rating.getCustomer().getLastname())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}