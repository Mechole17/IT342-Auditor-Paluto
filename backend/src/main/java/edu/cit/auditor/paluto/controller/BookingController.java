package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.BookingRequestDTO;
import edu.cit.auditor.paluto.dto.BookingResponseDTO;
import edu.cit.auditor.paluto.entity.Booking;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.BookingService;
import edu.cit.auditor.paluto.utils.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

            return ResponseUtility.success(request, HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseUtility.error("BK-001",e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/cooks/{cookId}/booked-dates")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getBookedDates(@PathVariable Long cookId) {
        try {
            List<LocalDate> bookedDates = bookingService.getBookedDates(cookId);
            return ResponseUtility.success(bookedDates, HttpStatus.OK);
        }catch (Exception e){
            return ResponseUtility.error("BUS-002","Failed to Fetch: " + e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getMyBookings(@PathVariable Long id) {
        try {
            List<BookingResponseDTO> bookings = bookingService.getCustomerBookings(id);

            return ResponseUtility.success(bookings,HttpStatus.OK);

        } catch (Exception e) {
           return ResponseUtility.error("BK-002","Failed to fetch bookings: " + e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}