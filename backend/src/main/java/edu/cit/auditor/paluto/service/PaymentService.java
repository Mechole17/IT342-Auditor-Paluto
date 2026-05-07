package edu.cit.auditor.paluto.service;

import lombok.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    // Hardcoded for your initial test
    private final String your_key = "";

    public Map<String, Object> createPaymongoCheckout(double amount, Long bookingId) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.paymongo.com/v1")
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((your_key + ":").getBytes()))
                .build();

        // Prepare the payload according to PayMongo API docs
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("line_items", List.of(Map.of(
                "amount", (int)(amount * 100), // Convert Pesos to Centavos
                "currency", "PHP",
                "name", "Paluto Cook Booking",
                "quantity", 1
        )));
        attributes.put("payment_method_types", List.of("card", "gcash", "paymaya"));
        attributes.put("description", "Payment for Paluto service id" + bookingId);

        // These are required by PayMongo to send the user back to your React app
        attributes.put("success_url", "http://localhost:3000/customer/bookings");
        attributes.put("cancel_url", "http://localhost:3000/customer/service-payment");

        Map<String, Object> body = Map.of("data", Map.of("attributes", attributes));

        return webClient.post()
                .uri("/checkout_sessions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
