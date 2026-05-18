package edu.cit.auditor.paluto.core.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cook_id", nullable = false)
    private Cook cook;

    private String title;
    private String fileUrl;      // Supabase URL

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    private String adminNote;

    private LocalDateTime uploadedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}