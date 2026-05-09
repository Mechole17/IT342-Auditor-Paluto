package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.BookingRequestDTO;
import edu.cit.auditor.paluto.dto.BookingResponseDTO;
import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.service.BookingService;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
            String customerEmail = authentication.getName();
             bookingService.createBooking(customerEmail, request);

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

    // 1. GET COOK BOOKINGS
    @GetMapping("/cook/{id}")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getCookBookings(@PathVariable Long id) {
        try {
            List<BookingResponseDTO> bookings = bookingService.getCookBookings(id);
            return ResponseUtility.success(bookings, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("BK-003", "Failed to fetch cook bookings: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 2. UPDATE BOOKING STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam String action) {
        try {
            bookingService.updateStatus(id, status, action);
            return ResponseUtility.success("Booking status updated to " + status, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("BK-004", "Failed to update status: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cook/{id}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCookStats(@PathVariable Long id) {
        try {
            Map<String, Object> stats = bookingService.getCookDashboardStats(id);
            return ResponseUtility.success(stats, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("ST-001", "Failed to fetch dashboard stats: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingById(
            @PathVariable Long id,
            java.security.Principal principal) {

        // principal.getName() is returning "cook@gmail.com"
        String email = principal.getName();

        // Hand the email string to the service
        return ResponseUtility.success(
                bookingService.getBookingDetails(id, email),
                HttpStatus.OK
        );
    }
}