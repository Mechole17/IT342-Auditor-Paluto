package edu.cit.auditor.paluto.core.events;

import edu.cit.auditor.paluto.core.entities.Booking;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.Map;

@Getter
public class BookingPaidEvent {
    private final Booking booking;
    private final String customerEmail;
    private final BigDecimal amountPaid;
    private final Map<String, Object> metadata;

    public BookingPaidEvent(Booking booking, String customerEmail, BigDecimal amountPaid, Map<String, Object> metadata) {
        this.booking = booking;
        this.customerEmail = customerEmail;
        this.amountPaid = amountPaid;
        this.metadata = metadata;
    }
}