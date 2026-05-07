package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.BookingRequestDTO;
import edu.cit.auditor.paluto.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    public Map<String, Object> createPaymongoCheckout(double amount, Map<String, Object> metadata) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.paymongo.com/v1")
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((paymongoKey + ":").getBytes()))
                .build();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("line_items", List.of(Map.of(
                "amount", (int)(amount * 100),
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
        Double amount = Double.valueOf(request.get("amount").toString());

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

    public void handleWebhook(Map<String, Object> payload) {
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        String eventType = (String) attributes.get("type");

        System.out.println("Webhook received: " + eventType);

        if (eventType.equals("checkout_session.payment.paid")) {
            Map<String, Object> eventData = (Map<String, Object>) attributes.get("data");
            Map<String, Object> eventAttributes = (Map<String, Object>) eventData.get("attributes");
            Map<String, Object> metadata = (Map<String, Object>) eventAttributes.get("metadata");

            // Build booking from metadata
            BookingRequestDTO bookingRequest = new BookingRequestDTO();
            bookingRequest.setServiceId(Long.valueOf(metadata.get("serviceId").toString()));
            bookingRequest.setQuantity(Integer.valueOf(metadata.get("quantity").toString()));
            bookingRequest.setServiceAddress(metadata.get("serviceAddress").toString());
            bookingRequest.setScheduledDate(metadata.get("scheduledDate").toString());
            bookingRequest.setScheduledTime(metadata.get("scheduledTime").toString());

            String customerEmail = metadata.get("customerEmail").toString();

            // Create booking with PAID_PENDING status
            bookingService.createBooking(customerEmail, bookingRequest);
        }
        // failed/expired - do nothing, no booking was created
    }
}
