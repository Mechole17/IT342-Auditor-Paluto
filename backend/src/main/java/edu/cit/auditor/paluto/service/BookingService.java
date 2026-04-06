package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.BookingRequestDTO;
import edu.cit.auditor.paluto.dto.BookingResponseDTO;
import edu.cit.auditor.paluto.entity.Booking;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.entity.User;
import edu.cit.auditor.paluto.entity.Service;
import edu.cit.auditor.paluto.repository.BookingRepository;
import edu.cit.auditor.paluto.repository.ServiceRepository;
import edu.cit.auditor.paluto.repository.UserRepository;
import lombok.RequiredArgsConstructor;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public Booking createBooking(Long customerId, BookingRequestDTO dto) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Service dish = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Cook cook = dish.getCook();
        int qty = dto.getQuantity();

        // 1. Ingredients Cost (Price * Quantity)
        BigDecimal ingredientsTotal = dish.getIngredientsCost()
                .multiply(BigDecimal.valueOf(qty));

        // 2. SMART SCALING PREP TIME (Matches React Logic)
        double basePrepTime = dish.getEstPrepTime();
        double totalPrepTimeMinutes;

        if (qty > 1) {
            // 1st set = 100%, extra sets = 20% each
            totalPrepTimeMinutes = basePrepTime + (basePrepTime * 0.20 * (qty - 1));
        } else {
            totalPrepTimeMinutes = basePrepTime;
        }

        // 3. Labor Cost calculation
        BigDecimal hourlyRate = BigDecimal.valueOf(cook.getHourly_rate());
        BigDecimal hours = BigDecimal.valueOf(totalPrepTimeMinutes)
                .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

        BigDecimal laborTotal = hourlyRate.multiply(hours);

        // 4. Final Total (Set to 2 decimal places to match UI)
        BigDecimal finalTotal = ingredientsTotal.add(laborTotal)
                .setScale(2, RoundingMode.HALF_UP);

        // 5. Build and Save
        Booking booking = Booking.builder()
                .customer(customer)
                .cook(cook)
                .service(dish)
                .quantity(qty)
                .totalAmount(finalTotal)
                .serviceAddress(dto.getServiceAddress())
                .scheduledDate(LocalDate.parse(dto.getScheduledDate()))
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

    public void updateStatus(Long bookingId, String status, String action) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

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
}
