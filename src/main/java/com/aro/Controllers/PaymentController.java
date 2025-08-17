package com.aro.Controllers;

import com.aro.DTOs.ErrorResponse;
import com.aro.DTOs.PaymentCallbackDto;
import com.aro.DTOs.SuccessResponse;
import com.aro.Services.PaymentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentsService paymentsService;

    public PaymentController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam("amount") int amount) {
        try {
            return ResponseEntity.ok(paymentsService.createOrder(amount));
        } catch (Exception e) {
            log.error("Failed to create Razorpay order", e);
            return ResponseEntity.badRequest().body("Unable to create order: " + e.getMessage());
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handlePaymentCallback(@RequestBody PaymentCallbackDto dto) {
        try {
            boolean isVerified = paymentsService.processPaymentCallback(dto);

            if (isVerified) {
                return ResponseEntity.ok(new SuccessResponse("Payment done successfully", LocalDateTime.now().toString()));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Payment_failed", "Payment failed due do some error", LocalDateTime.now()
                    .toString()));
            }
        } catch (Exception e) {
            log.error("Exception in payment callback", e);
            return ResponseEntity.badRequest().body("Exception in payment callback");
        }
    }

}
