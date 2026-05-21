package edu.cit.auditor.paluto.payment;

import edu.cit.auditor.paluto.booking.BookingRequestDTO;
import edu.cit.auditor.paluto.booking.BookingService;
import edu.cit.auditor.paluto.core.entities.Booking;
import edu.cit.auditor.paluto.core.entities.Payment;
import edu.cit.auditor.paluto.core.repositories.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private BookingService bookingService;

    // FIXED: Added the missing repository mock dependency to eliminate the runtime crash
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    // FIXED: Rewrote structural mapper to pack nested payments array data context mirroring PayMongo specs
    private Map<String, Object> buildPayload(String eventType) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("customerEmail", "customer@example.com");
        metadata.put("serviceId", "1");
        metadata.put("quantity", "2");
        metadata.put("serviceAddress", "123 Test St");
        metadata.put("scheduledDate", "2026-06-01");
        metadata.put("scheduledTime", "10:00:00");

        Map<String, Object> source = Map.of("type", "gcash");
        Map<String, Object> paymentAttributes = Map.of(
                "amount", 150000,
                "source", source
        );
        Map<String, Object> paymentNode = Map.of(
                "id", "pay_test123",
                "attributes", paymentAttributes
        );

        Map<String, Object> checkoutAttributes = Map.of(
                "metadata", metadata,
                "payments", List.of(paymentNode)
        );
        Map<String, Object> checkoutData = Map.of(
                "id", "cs_test123",
                "attributes", checkoutAttributes
        );

        return Map.of(
                "data", Map.of(
                        "attributes", Map.of(
                                "type", eventType,
                                "data", checkoutData
                        )
                )
        );
    }

    @Test
    void shouldCreateBookingOnPaymentSuccess() {
        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        mockBooking.setStatus("PAID_PENDING");

        when(bookingService.createBooking(anyString(), any(BookingRequestDTO.class)))
                .thenReturn(mockBooking);
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(i -> i.getArgument(0));

        Map<String, Object> payload = buildPayload("checkout_session.payment.paid");

        assertDoesNotThrow(() -> paymentService.handleWebhook(payload));

        verify(bookingService, times(1)).createBooking(
                eq("customer@example.com"),
                any(BookingRequestDTO.class)
        );
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void shouldNotCreateBookingOnPaymentFailed() {
        Map<String, Object> payload = buildPayload("checkout_session.payment.expired");

        assertDoesNotThrow(() -> paymentService.handleWebhook(payload));
        verify(bookingService, never()).createBooking(anyString(), any(BookingRequestDTO.class));
    }

    @Test
    void shouldNotCreateBookingOnUnknownEvent() {
        Map<String, Object> payload = buildPayload("unknown.event");

        assertDoesNotThrow(() -> paymentService.handleWebhook(payload));
        verify(bookingService, never()).createBooking(anyString(), any(BookingRequestDTO.class));
    }
}