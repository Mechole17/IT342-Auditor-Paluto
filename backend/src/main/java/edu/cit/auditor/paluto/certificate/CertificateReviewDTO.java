package edu.cit.auditor.paluto.certificate;

import lombok.Data;

@Data
public class CertificateReviewDTO {
    private String status;    // APPROVED or REJECTED
    private String adminNote; // optional rejection reason
}