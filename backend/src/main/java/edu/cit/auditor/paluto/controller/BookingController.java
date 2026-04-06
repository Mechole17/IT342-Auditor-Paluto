package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.BookingRequestDTO;
import edu.cit.auditor.paluto.dto.BookingResponseDTO;
import edu.cit.auditor.paluto.entity.Booking;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 1. CREATE BOOKING (Customer only)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BookingRequestDTO>> createBooking(
            @RequestBody BookingRequestDTO request,
            Authentication authentication) {
        try {
            Long customerId = Long.parseLong(authentication.getName());


             bookingService.createBooking(customerId, request);

            // Wrap it exactly like ServiceController
            return new ResponseEntity<>(ApiResponse.<BookingRequestDTO>builder()
                    .success(true)
                    .data(request) // <--- RequestDTO inside the ApiResponse
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.<BookingRequestDTO>builder()
                    .success(false)
                    .error(ApiError.builder()
                            .code("BK-001")
                            .message(e.getMessage())
                            .build())
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getMyBookings(@PathVariable Long id) {
        try {
            List<BookingResponseDTO> bookings = bookingService.getCustomerBookings(id);

            return ResponseEntity.ok(ApiResponse.<List<BookingResponseDTO>>builder()
                    .success(true)
                    .data(bookings)
                    .timestamp(LocalDateTime.now().toString())
                    .build());

        } catch (Exception e) {
            // FIXED: Corrected the Generic types and ensured the builder matches the Response DTO list
            return new ResponseEntity<>(
                    ApiResponse.<List<BookingResponseDTO>>builder()
                            .success(false)
                            .error(ApiError.builder()
                                    .code("BK-001")
                                    .message("Failed to fetch bookings: " + e.getMessage())
                                    .build())
                            .timestamp(LocalDateTime.now().toString())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}