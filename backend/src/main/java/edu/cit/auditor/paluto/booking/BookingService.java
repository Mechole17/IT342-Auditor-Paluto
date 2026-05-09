package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.core.entities.*;
import edu.cit.auditor.paluto.dto.BookingRequestDTO;
import edu.cit.auditor.paluto.dto.BookingResponseDTO;
import edu.cit.auditor.paluto.core.repositories.BookingRepository;
import edu.cit.auditor.paluto.core.repositories.ServiceRepository;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import edu.cit.auditor.paluto.strategy.PricingStrategy;
import edu.cit.auditor.paluto.strategy.ScaledPricingStrategy;
import edu.cit.auditor.paluto.strategy.StandardPricingStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public void verifyCookAvailability(Long cookId, LocalDate date) {
        List<String> activeStatuses = List.of("PAID_PENDING", "ACCEPTED", "COMPLETED");

        boolean isBusy = bookingRepository.existsByCookIdAndScheduledDateAndStatusIn(
                cookId, date, activeStatuses);

        if (isBusy) {
            throw new RuntimeException("Validation Error: Cook is already booked for this day.");
        }
    }

    public List<LocalDate> getBookedDates(Long cookId) {
        // Only return dates for active bookings
        return bookingRepository.findBookedDatesByCookId(cookId);
    }
    @Transactional
    public Booking createBooking(String customerEmail, BookingRequestDTO dto) {
        Customer customer = (Customer) userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Service dish = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Cook cook = dish.getCook();

        LocalDate requestedDate = LocalDate.parse(dto.getScheduledDate());
        verifyCookAvailability(cook.getId(), requestedDate); //Schedule conflict Resolution

        int qty = dto.getQuantity();

        // STEP 1: Select Strategy based on Quantity
        PricingStrategy strategy = (qty > 1)
                ? new ScaledPricingStrategy()
                : new StandardPricingStrategy();

        // STEP 2: Use Strategy for Prep Time
        int totalPrepTimeMinutes = strategy.calculatePrepTime(dish.getEstPrepTime(), qty);

        // STEP 3: Use Strategy for Labor Cost
        BigDecimal hourlyRate = (cook.getHourlyRate() != null) ? cook.getHourlyRate() : BigDecimal.ZERO;
        BigDecimal laborTotal = strategy.calculateLaborTotal(hourlyRate, totalPrepTimeMinutes);

        // STEP 4: Ingredients and Final Total
        BigDecimal ingredientsTotal = dish.getIngredientsCost().multiply(BigDecimal.valueOf(qty));
        BigDecimal finalTotal = ingredientsTotal.add(laborTotal).setScale(2, RoundingMode.HALF_UP);

        // STEP 5: Build and Save
        Booking booking = Booking.builder()
                .customer(customer)
                .cook(cook)
                .service(dish)
                .quantity(qty)
                .totalAmount(finalTotal)
                .serviceAddress(dto.getServiceAddress())
                .scheduledDate(requestedDate)
                .scheduledTime(LocalTime.parse(dto.getScheduledTime()))
                .status("PAID_PENDING")
                .build();

        return bookingRepository.save(booking);
    }

    public List<BookingResponseDTO> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(booking -> BookingResponseDTO.builder()
                        .id(booking.getId())
                        .serviceTitle(booking.getService().getTitle())
                        .serviceImage(booking.getService().getImageUrl())
                        .quantity(booking.getQuantity())
                        .totalAmount(booking.getTotalAmount())
                        .scheduledDate(booking.getScheduledDate().toString())
                        .scheduledTime(booking.getScheduledTime().toString())
                        .status(booking.getStatus() != null ? booking.getStatus().toString() : "PENDING")
                        .build())
                .collect(Collectors.toList());
    }

    public void handlePaymentSuccess(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        booking.setStatus("PAID_PENDING");
        bookingRepository.save(booking);
    }

    public void handlePaymentFailed(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        booking.setStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    public void updateStatus(Long bookingId, String status, String action) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("REJECT".equalsIgnoreCase(action)) {
            // Logic for triggering PayMongo refund would go here
            booking.setStatus("REJECTED_REFUNDED");
        } else if ("COMPLETE".equalsIgnoreCase(action)) {
            LocalDateTime now = LocalDateTime.now();
            // Combine date and time from the booking entity
            LocalDateTime schedule = LocalDateTime.of(booking.getScheduledDate(), booking.getScheduledTime());

            if (now.isBefore(schedule)) {
                throw new IllegalStateException("Cannot complete a booking before the scheduled time.");
            }
        } else {
            booking.setStatus(status);
        }

        booking.setStatus(status);
        LocalDateTime now = LocalDateTime.now();

        switch (action) {
            case "ACCEPT" -> booking.setAcceptedAt(now);
            case "REJECT" -> booking.setRejectedAt(now);
            case "COMPLETE" -> booking.setCompletedAt(now);
            case "CANCEL" -> booking.setCancelledAt(now);
        }

        bookingRepository.save(booking);
    }

    public List<BookingResponseDTO> getCookBookings(Long cookId) {
        return bookingRepository.findByCookIdOrderByCreatedAtDesc(cookId).stream()
                .map(booking -> BookingResponseDTO.builder()
                        .id(booking.getId())
                        // You can add customer details here later if you update your DTO
                        .serviceTitle(booking.getService().getTitle())
                        .serviceImage(booking.getService().getImageUrl())
                        .quantity(booking.getQuantity())
                        .totalAmount(booking.getTotalAmount())
                        .scheduledDate(booking.getScheduledDate().toString())
                        .scheduledTime(booking.getScheduledTime().toString())
                        .status(booking.getStatus() != null ? booking.getStatus() : "PAID_PENDING")
                        .customerName(booking.getCustomer().getFirstname() + " " + booking.getCustomer().getLastname())
                        .serviceAddress(booking.getServiceAddress())
                        .build())
                .collect(Collectors.toList());
    }

    public Map<String, Object> getCookDashboardStats(Long cookId) {
        List<BookingResponseDTO> bookings = getCookBookings(cookId);

        long completedBookings = bookings.stream()
                .filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus()) ||
                        "ACCEPTED".equalsIgnoreCase(b.getStatus()))
                .count();

        long upcoming = bookings.stream()
                .filter(b -> "ACCEPTED".equalsIgnoreCase(b.getStatus()))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("completedBookings", completedBookings);
        stats.put("upcomingBookings", upcoming);
        stats.put("averageRating", 4.8); // Placeholder

        return stats;
    }

    // Add this method to your BookingService class
    public BookingResponseDTO getBookingDetails(Long id, String email) {
        // 1. Find the user by email to get their ID
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long currentUserId = user.getId();

        // 2. Find the booking
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 3. Secure Ownership Check
        if (!booking.getCustomer().getId().equals(currentUserId) &&
                !booking.getCook().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized access to booking details");
        }

        // 4. Map and Return (using your existing builder)
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .serviceTitle(booking.getService().getTitle())
                .serviceImage(booking.getService().getImageUrl())
                .quantity(booking.getQuantity())
                .totalAmount(booking.getTotalAmount())
                .scheduledDate(booking.getScheduledDate().toString())
                .scheduledTime(booking.getScheduledTime().toString())
                .status(booking.getStatus())
                .serviceAddress(booking.getServiceAddress())
                .customerName(booking.getCustomer().getFirstname() + " " + booking.getCustomer().getLastname())
                .cookName(booking.getCook().getFirstname() + " " + booking.getCook().getLastname())
                .createdAt(booking.getCreatedAt() != null ? booking.getCreatedAt().toString() : null)
                .acceptedAt(booking.getAcceptedAt() != null ? booking.getAcceptedAt().toString() : null)
                .completedAt(booking.getCompletedAt() != null ? booking.getCompletedAt().toString() : null)
                .rejectedAt(booking.getRejectedAt() != null ? booking.getRejectedAt().toString() : null)
                .build();
    }
}
