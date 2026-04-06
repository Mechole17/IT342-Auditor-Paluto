package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.BookingRequestDTO;
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

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public Booking createBooking(Long customerId, BookingRequestDTO dto) {
        // 1. Fetch the Customer
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 2. Fetch the Dish (using explicit Entity path to avoid @Service conflict)
        Service dish = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Cook cook = dish.getCook();

        // 3. MATH: Ingredients Cost (Price * Quantity)
        BigDecimal ingredientsTotal = dish.getIngredientsCost()
                .multiply(BigDecimal.valueOf(dto.getQuantity()));

        // 4. MATH: Labor Cost (Rate * (Minutes / 60))
        BigDecimal hourlyRate = BigDecimal.valueOf(cook.getHourly_rate());
        BigDecimal prepMinutes = BigDecimal.valueOf(dish.getEstPrepTime());

        // Convert minutes to hours (e.g., 90 / 60 = 1.5)
        BigDecimal hours = prepMinutes.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal laborTotal = hourlyRate.multiply(hours);

        // 5. Final Total
        BigDecimal finalTotal = ingredientsTotal.add(laborTotal);

        // 6. Build and Save
        Booking booking = Booking.builder()
                .customer(customer)
                .cook(cook)
                .service(dish)
                .quantity(dto.getQuantity())
                .totalAmount(finalTotal)
                .serviceAddress(dto.getServiceAddress())
                .scheduledDate(LocalDate.parse(dto.getScheduledDate()))
                .scheduledTime(LocalTime.parse(dto.getScheduledTime()))
                .status("PAID_PENDING")
                .build();

        return bookingRepository.save(booking);
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
