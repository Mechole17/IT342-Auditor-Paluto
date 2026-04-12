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
import edu.cit.auditor.paluto.strategy.PricingStrategy;
import edu.cit.auditor.paluto.strategy.ScaledPricingStrategy;
import edu.cit.auditor.paluto.strategy.StandardPricingStrategy;
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

        // STEP 1: Select Strategy based on Quantity
        PricingStrategy strategy = (qty > 1)
                ? new ScaledPricingStrategy()
                : new StandardPricingStrategy();

        // STEP 2: Use Strategy for Prep Time
        double totalPrepTimeMinutes = strategy.calculatePrepTime(dish.getEstPrepTime(), qty);

        // STEP 3: Use Strategy for Labor Cost
        BigDecimal hourlyRate = BigDecimal.valueOf(cook.getHourly_rate());
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
