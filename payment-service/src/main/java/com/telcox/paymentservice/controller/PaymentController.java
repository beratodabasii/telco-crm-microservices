package com.telcox.paymentservice.controller;

import com.telcox.paymentservice.dto.CreatePaymentRequest;
import com.telcox.paymentservice.dto.PaymentResponse;
import com.telcox.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(@RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @PostMapping("/{paymentId}/complete")
    public PaymentResponse completePayment(@PathVariable Long paymentId) {
        return paymentService.completePayment(paymentId);
    }

    @PostMapping("/{paymentId}/fail")
    public PaymentResponse failPayment(@PathVariable Long paymentId, @RequestBody String reason) {
        return paymentService.failPayment(paymentId, reason);
    }

}
