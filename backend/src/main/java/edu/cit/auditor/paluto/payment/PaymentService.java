package edu.cit.auditor.paluto.payment;

import edu.cit.auditor.paluto.booking.BookingRequestDTO;
import edu.cit.auditor.paluto.booking.BookingService;
import edu.cit.auditor.paluto.core.entities.Booking;
import edu.cit.auditor.paluto.core.entities.Payment;
import edu.cit.auditor.paluto.core.events.BookingPaidEvent;
import edu.cit.auditor.paluto.core.repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${paymongo.test.key}")
    private String paymongoKey;

    private final BookingService bookingService;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Map<String, Object> createPaymongoCheckout(BigDecimal amount, Map<String, Object> metadata) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.paymongo.com/v1")
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((paymongoKey + ":").getBytes()))
                .build();

        // FIXED: Clean conversion into whole integer cents without precision drop-off
        int amountInCents = amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("line_items", List.of(Map.of(
                "amount", amountInCents,
                "currency", "PHP",
                "name", "Paluto Cook Booking",
                "quantity", 1
        )));
        attributes.put("payment_method_types", List.of("card", "gcash", "paymaya"));
        attributes.put("metadata", metadata); // booking details stored here
        attributes.put("success_url", "http://localhost:3000/customer/bookings?payment=success");
        attributes.put("cancel_url", "http://localhost:3000/customer/service-payment");

        Map<String, Object> body = Map.of("data", Map.of("attributes", attributes));

        return webClient.post()
                .uri("/checkout_sessions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public Map<String, Object> initiateCheckout(String email, Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString()).setScale(2, RoundingMode.HALF_UP);

        // Store booking details in metadata instead of creating booking
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("customerEmail", email);
        metadata.put("serviceId", request.get("serviceId").toString());
        metadata.put("quantity", request.get("quantity").toString());
        metadata.put("serviceAddress", request.get("serviceAddress").toString());
        metadata.put("scheduledDate", request.get("scheduledDate").toString());
        metadata.put("scheduledTime", request.get("scheduledTime").toString());

        return createPaymongoCheckout(amount, metadata);
    }

    @Transactional
    public void handleWebhook(Map<String, Object> payload) {
        System.out.println(payload);
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        String eventType = (String) attributes.get("type");

        System.out.println("Webhook received: " + eventType);

        // FIX 1: Listen ONLY to checkout_session completions to stop duplicate insertions completely
        if ("checkout_session.payment.paid".equals(eventType)) {
            Map<String, Object> eventData = (Map<String, Object>) attributes.get("data");
            String checkoutSessionId = eventData.get("id") != null ? eventData.get("id").toString() : "UNKNOWN_SESSION";

            Map<String, Object> eventAttributes = (Map<String, Object>) eventData.get("attributes");
            Map<String, Object> metadata = (Map<String, Object>) eventAttributes.get("metadata");

            if (metadata == null) {
                System.out.println("Skipping: No metadata found in checkout session payload.");
                return;
            }

            // Build booking request from checkout session metadata fields
            BookingRequestDTO bookingRequest = new BookingRequestDTO();
            bookingRequest.setServiceId(Long.valueOf(metadata.get("serviceId").toString()));
            bookingRequest.setQuantity(Integer.valueOf(metadata.get("quantity").toString()));
            bookingRequest.setServiceAddress(metadata.get("serviceAddress").toString());
            bookingRequest.setScheduledDate(metadata.get("scheduledDate").toString());
            bookingRequest.setScheduledTime(metadata.get("scheduledTime").toString());

            String customerEmail = metadata.get("customerEmail").toString();

            // 1. Save core booking data locally
            Booking savedBooking = bookingService.createBooking(customerEmail, bookingRequest);

            // Default safe fallbacks for financial parameters
            BigDecimal amountPaid = BigDecimal.ZERO;
            String paymentSourceType = "unknown";
            String paymongoPaymentId = "UNKNOWN_PAYMENT";

            // FIX 2: Safely extract nested financial structures from Checkout Session payments array
            List<Map<String, Object>> paymentsList = (List<Map<String, Object>>) eventAttributes.get("payments");
            if (paymentsList != null && !paymentsList.isEmpty()) {
                Map<String, Object> firstPayment = paymentsList.get(0);

                // FIXED: Extract the actual payment object ID (e.g., pay_abc123)
                if (firstPayment.get("id") != null) {
                    paymongoPaymentId = firstPayment.get("id").toString();
                }

                Map<String, Object> paymentAttributes = (Map<String, Object>) firstPayment.get("attributes");

                if (paymentAttributes != null) {
                    // Extract precise transaction value from cents
                    if (paymentAttributes.get("amount") != null) {
                        BigDecimal rawCentsValue = new BigDecimal(paymentAttributes.get("amount").toString());
                        amountPaid = rawCentsValue.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }

                    // Extract exact payment medium dynamically (card, gcash, paymaya)
                    if (paymentAttributes.get("source") != null) {
                        Map<String, Object> sourceMap = (Map<String, Object>) paymentAttributes.get("source");
                        if (sourceMap.get("type") != null) {
                            paymentSourceType = sourceMap.get("type").toString();
                        }
                    }
                }
            }

            // 2. Instantiate and connect distinct payment record
            Payment paymentLog = Payment.builder()
                    .bookingId(savedBooking.getId())
                    .paymongoCheckoutSessionId(checkoutSessionId)
                    .transactionReference(paymongoPaymentId)
                    .amountPaid(amountPaid)
                    .paymentStatus("PAID")
                    .fundingSource(paymentSourceType)
                    .build();

            // 3. Commit row to payments database table
            paymentRepository.save(paymentLog);
            System.out.println("SUCCESS: Cleanly saved Booking ID " + savedBooking.getId() + " and linked Payment Log.");

            eventPublisher.publishEvent(new BookingPaidEvent(savedBooking, customerEmail, amountPaid, metadata));
            System.out.println("Payment confirmation event broadcasted to platform observers.");
        } else {
            System.out.println("Ignored Event Type: " + eventType);
        }
    }
}
