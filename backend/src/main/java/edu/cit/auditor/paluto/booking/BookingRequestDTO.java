package edu.cit.auditor.paluto.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private Long serviceId;
    private int quantity;
    private String serviceAddress;
    private String scheduledDate; // Format: "yyyy-MM-dd"
    private String scheduledTime; // Format: "HH:mm"
}
