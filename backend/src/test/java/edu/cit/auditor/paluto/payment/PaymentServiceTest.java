package edu.cit.auditor.paluto.payment;

import edu.cit.auditor.paluto.booking.BookingRequestDTO;
import edu.cit.auditor.paluto.booking.BookingService;
import edu.cit.auditor.paluto.core.entities.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private PaymentService paymentService;

    private Map<String, Object> buildPayload(String eventType) {
        return Map.of(
                "data", Map.of(
                        "attributes", Map.of(
                                "type", eventType,
                                "data", Map.of(
                                        "attributes", Map.of(
                                                "metadata", Map.of(
                                                        "customerEmail", "customer@example.com",
                                                        "serviceId", "1",
                                                        "quantity", "2",
                                                        "serviceAddress", "123 Test St",
                                                        "scheduledDate", "2026-06-01",
                                                        "scheduledTime", "10:00:00"
                                                )
                                        )
                                )
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

        Map<String, Object> payload = buildPayload("checkout_session.payment.paid");

        assertDoesNotThrow(() -> paymentService.handleWebhook(payload));
        verify(bookingService, times(1)).createBooking(
                eq("customer@example.com"),
                any(BookingRequestDTO.class)
        );
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