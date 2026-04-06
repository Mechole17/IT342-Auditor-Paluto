package edu.cit.auditor.paluto.dto;

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
}
