package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.BookingRequestDTO;
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

@RestController
@RequestMapping("/api/bookings")
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
}