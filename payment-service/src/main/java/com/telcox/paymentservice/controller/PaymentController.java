package com.telcox.paymentservice.controller;

import com.telcox.paymentservice.entity.Payment;
import com.telcox.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentService.createPayment(payment);
    }

    @PostMapping("/{paymentId}/complete")
    public Payment completePayment(@PathVariable Long paymentId) {
        return paymentService.completePayment(paymentId);
    }

    @PostMapping("/{paymentId}/fail")
    public Payment failPayment(@PathVariable Long paymentId, @RequestBody String reason) {
        return paymentService.failPayment(paymentId, reason);
    }

}
