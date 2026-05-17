package edu.cit.auditor.paluto.booking;

import edu.cit.auditor.paluto.core.entities.*;
import edu.cit.auditor.paluto.core.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Customer mockCustomer;
    private Cook mockCook;
    private Service mockService;

    @BeforeEach
    void setUp() {
        mockCook = new Cook();
        mockCook.setId(1L);
        mockCook.setHourlyRate(new BigDecimal("450.00"));

        mockCustomer = new Customer();
        mockCustomer.setEmail("customer@example.com");

        mockService = new Service();
        mockService.setId(1L);
        mockService.setCook(mockCook);
        mockService.setIngredientsCost(new BigDecimal("200.00"));
        mockService.setEstPrepTime(60);
        mockService.setTitle("Adobo");
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        when(userRepository.findByEmail("customer@example.com"))
                .thenReturn(Optional.of(mockCustomer));
        when(serviceRepository.findById(1L))
                .thenReturn(Optional.of(mockService));
        when(bookingRepository.existsByCookIdAndScheduledDateAndStatusIn(
                anyLong(), any(), anyList()))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(i -> i.getArgument(0));

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setServiceId(1L);
        dto.setQuantity(1);
        dto.setServiceAddress("123 Test St");
        dto.setScheduledDate(LocalDate.now().plusDays(1).toString());
        dto.setScheduledTime("10:00:00");

        Booking result = bookingService.createBooking("customer@example.com", dto);

        assertNotNull(result);
        assertEquals("PAID_PENDING", result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenCookAlreadyBooked() {
        when(userRepository.findByEmail("customer@example.com"))
                .thenReturn(Optional.of(mockCustomer));
        when(serviceRepository.findById(1L))
                .thenReturn(Optional.of(mockService));
        when(bookingRepository.existsByCookIdAndScheduledDateAndStatusIn(
                anyLong(), any(), anyList()))
                .thenReturn(true);

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setServiceId(1L);
        dto.setQuantity(1);
        dto.setServiceAddress("123 Test St");
        dto.setScheduledDate(LocalDate.now().plusDays(1).toString());
        dto.setScheduledTime("10:00:00");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                bookingService.createBooking("customer@example.com", dto)
        );

        assertTrue(ex.getMessage().contains("already booked"));
    }

    @Test
    void shouldUpdateBookingStatusToAccepted() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("PAID_PENDING");

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(i -> i.getArgument(0));

        bookingService.updateStatus(1L, "ACCEPTED", "ACCEPT");

        assertEquals("ACCEPTED", booking.getStatus());
        assertNotNull(booking.getAcceptedAt());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void shouldUpdateBookingStatusToRejected() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("PAID_PENDING");

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(i -> i.getArgument(0));

        bookingService.updateStatus(1L, "REJECTED_REFUNDED", "REJECT");

        assertEquals("REJECTED_REFUNDED", booking.getStatus());
        assertNotNull(booking.getRejectedAt());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void shouldThrowExceptionWhenCompletingBeforeScheduledTime() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("ACCEPTED");
        booking.setScheduledDate(LocalDate.now().plusDays(1)); // future date
        booking.setScheduledTime(LocalTime.of(10, 0));

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () ->
                bookingService.updateStatus(1L, "COMPLETED", "COMPLETE")
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldCompleteBookingAfterScheduledTime() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("ACCEPTED");
        booking.setScheduledDate(LocalDate.now().minusDays(1)); // past date
        booking.setScheduledTime(LocalTime.of(10, 0));

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() ->
                bookingService.updateStatus(1L, "COMPLETED", "COMPLETE")
        );

        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void shouldNotAllowBookingIfCookAlreadyBookedOnSameDay() {
        when(userRepository.findByEmail("customer@example.com"))
                .thenReturn(Optional.of(mockCustomer));
        when(serviceRepository.findById(1L))
                .thenReturn(Optional.of(mockService));
        when(bookingRepository.existsByCookIdAndScheduledDateAndStatusIn(
                anyLong(), any(), anyList()))
                .thenReturn(true);

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setServiceId(1L);
        dto.setQuantity(1);
        dto.setServiceAddress("123 Test St");
        dto.setScheduledDate(LocalDate.now().plusDays(1).toString());
        dto.setScheduledTime("10:00:00");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                bookingService.createBooking("customer@example.com", dto)
        );

        assertTrue(ex.getMessage().contains("already booked"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}