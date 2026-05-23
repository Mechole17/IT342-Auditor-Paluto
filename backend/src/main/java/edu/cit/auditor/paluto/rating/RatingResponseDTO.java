package edu.cit.auditor.paluto.rating;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponseDTO {
    private Long id;
    private Long bookingId;
    private Long cookId;
    private String customerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}