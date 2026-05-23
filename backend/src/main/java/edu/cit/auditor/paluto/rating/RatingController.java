package edu.cit.auditor.paluto.rating;

import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<RatingResponseDTO>> submitRating(
            @RequestBody RatingRequestDTO dto,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            RatingResponseDTO result = ratingService.submitRating(email, dto);
            return ResponseUtility.success(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseUtility.error("RAT-001", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cook/{cookId}")
    public ResponseEntity<ApiResponse<List<RatingResponseDTO>>> getCookRatings(
            @PathVariable Long cookId) {
        try {
            List<RatingResponseDTO> ratings = ratingService.getCookRatings(cookId);
            return ResponseUtility.success(ratings, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("RAT-002", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cook/{cookId}/average")
    public ResponseEntity<ApiResponse<Double>> getCookAverageRating(
            @PathVariable Long cookId) {
        try {
            Double avg = ratingService.getCookAverageRating(cookId);
            return ResponseUtility.success(avg, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("RAT-003", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check/{bookingId}")
    public ResponseEntity<ApiResponse<Boolean>> checkIfRated(
            @PathVariable Long bookingId) {
        try {
            boolean rated = ratingService.hasRated(bookingId);
            return ResponseUtility.success(rated, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("RAT-004", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}