package edu.cit.auditor.paluto.payment;

import edu.cit.auditor.paluto.core.entities.Payment;
import edu.cit.auditor.paluto.core.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RefundService {

    @Value("${paymongo.test.key}")
    private String paymongoKey;

    private final PaymentRepository paymentRepository;

    public void processRefund(Long bookingId, String reason) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment record not found for booking: " + bookingId));

        // 🛡️ DEFENSIVE GUARD: Prevent duplicate PayMongo calls if already refunded
        if ("REFUNDED".equalsIgnoreCase(payment.getPaymentStatus())) {
            System.out.println("Payment already marked as REFUNDED for booking: " + bookingId);
            return;
        }

        int amountInCents = payment.getAmountPaid()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.paymongo.com/v1")
                .defaultHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((paymongoKey + ":").getBytes()))
                .build();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("amount", amountInCents);
        attributes.put("payment_id", payment.getTransactionReference());
        attributes.put("reason", "requested_by_customer");

        Map<String, Object> body = Map.of("data", Map.of("attributes", attributes));

        try {
            webClient.post()
                    .uri("/refunds")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            payment.setPaymentStatus("REFUNDED");
            payment.setRefundedAt(LocalDateTime.now());
            payment.setRefundReason(reason);
            paymentRepository.save(payment);

            System.out.println("Refund processed for booking: " + bookingId);

        } catch (Exception e) {
            System.err.println("Refund failed: " + e.getMessage());
            throw new RuntimeException("Refund processing failed: " + e.getMessage());
        }
    }
}