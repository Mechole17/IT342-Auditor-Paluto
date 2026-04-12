package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.utils.ResponseUtility;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/payment")
public class PaymentController {

//    @PostMapping("/checkout")
//    public ResponseEntity<?> testCheckout() {
//
//        String authString = sKey + ":"; // Don't forget the colon!
//
//        String secretKey = Base64.getEncoder().encodeToString(authString.getBytes());
//        String url = "https://api.paymongo.com/v1/checkout_sessions";
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 1. Set Headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Basic " + secretKey);
//
//        // 2. Create Minimal Test Payload
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("send_email_receipt", false);
//        attributes.put("show_description", true);
//        attributes.put("payment_method_types", List.of("gcash", "grab_pay", "paymaya", "card"));
//
//        attributes.put("line_items", List.of(Map.of(
//                "amount", 10000, // 100.00 PHP (PayMongo uses centavos)
//                "currency", "PHP",
//                "name", "Test Paluto Booking",
//                "quantity", 1
//        )));
//
//        // Redirect URLs (can be dummy for now)
//        attributes.put("success_url", "http://localhost:3000/success");
//        attributes.put("cancel_url", "http://localhost:3000/cancel");
//
//        Map<String, Object> data = Map.of("data", Map.of("attributes", attributes));
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);
//
//        try {
//            // 3. Send Request
//            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
//
//            // 4. Extract the checkout_url to send back to your frontend/Postman
//            Map<String, Object> responseBody = response.getBody();
//            return ResponseUtility.success(responseBody, HttpStatus.OK);
//        } catch (Exception e) {
//            return ResponseUtility.error("PMT-001", "Payment Error:" + e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
}
