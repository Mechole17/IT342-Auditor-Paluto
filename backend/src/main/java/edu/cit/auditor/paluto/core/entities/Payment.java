package edu.cit.auditor.paluto.core.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "paymongo_checkout_session_id")
    private String paymongoCheckoutSessionId;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    private String paymentStatus; // PAID, REFUNDED, FAILED

    private String fundingSource; // gcash, maya, card

    private LocalDateTime processedAt;

    @PrePersist
    protected void onProcess() {
        this.processedAt = LocalDateTime.now();
    }
}