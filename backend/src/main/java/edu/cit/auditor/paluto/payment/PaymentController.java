package edu.cit.auditor.paluto.payment;

import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkout(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            String email = authentication.getName();
            Map<String, Object> paymongoData = paymentService.initiateCheckout(email, request);
            return ResponseUtility.success(paymongoData, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("PAY-001", "Payment Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            paymentService.handleWebhook(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Webhook error: " + e.getMessage());
            return ResponseEntity.ok().build();
        }
    }
}
