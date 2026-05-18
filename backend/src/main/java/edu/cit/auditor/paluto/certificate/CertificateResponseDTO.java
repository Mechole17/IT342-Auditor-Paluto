package edu.cit.auditor.paluto.certificate;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CertificateResponseDTO {
    private Long id;
    private Long cookId;
    private String cookName;
    private String title;
    private String fileUrl;
    private String status;
    private String adminNote;
    private LocalDateTime uploadedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime deletedAt;
}