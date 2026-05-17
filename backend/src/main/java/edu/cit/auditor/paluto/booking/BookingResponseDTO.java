package edu.cit.auditor.paluto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private String serviceTitle;
    private String serviceImage;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String scheduledDate;
    private String scheduledTime;
    private String status; // PENDING, ACCEPTED, etc.
    private String customerName;
    private String cookName;     // Add this
    private String serviceAddress;

    private String acceptedAt;
    private String rejectedAt;
    private String completedAt;
    private String cancelledAt;
    private String createdAt;
}
