package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.PaymentService;
import edu.cit.auditor.paluto.utils.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkout(@RequestBody Map<String, Object> request) {
        try {
            Double amount = Double.valueOf(request.get("amount").toString());
            Long serviceId = Long.valueOf(request.get("serviceId").toString()); // CHANGED: was bookingId

            Map<String, Object> paymongoData = paymentService.createPaymongoCheckout(amount, serviceId);

            // Using your utility for success
            return ResponseUtility.success(paymongoData, HttpStatus.OK);

        } catch (Exception e) {
            // Using your utility for error with the SDD code PAY-001
            return ResponseUtility.error("PAY-001", "Payment Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
