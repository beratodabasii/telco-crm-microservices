package com.telcox.paymentservice.service;

import com.telcox.paymentservice.entity.OutboxEvent;
import com.telcox.paymentservice.entity.Payment;
import com.telcox.paymentservice.enums.PaymentStatus;
import com.telcox.paymentservice.event.PaymentCompletedEvent;
import com.telcox.paymentservice.event.PaymentFailedEvent;
import com.telcox.paymentservice.repository.OutboxEventRepository;
import com.telcox.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public Payment createPayment(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
    @Transactional
    public Payment completePayment(Long paymentId) {
       Payment paymentToComplete = paymentRepository.findById(paymentId).orElseThrow(
               ()-> new RuntimeException("Payment not found")
       );
       paymentToComplete.setStatus(PaymentStatus.COMPLETED);
       paymentRepository.save(paymentToComplete);
        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent();
        paymentCompletedEvent.setPaymentId(paymentId);
        paymentCompletedEvent.setOrderId(paymentToComplete.getOrderId());
        paymentCompletedEvent.setAmount(paymentToComplete.getAmount());
        paymentCompletedEvent.setCurrency(paymentToComplete.getCurrency());
        paymentCompletedEvent.setCompletedAt(LocalDateTime.now());
        String payload = objectMapper.writeValueAsString(paymentCompletedEvent);
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setPublished(false);
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setEventType("PaymentCompletedEvent");
        outboxEventRepository.save(outboxEvent);
       return paymentToComplete;
    }

    @Transactional
    public Payment failPayment(Long paymentId, String reason) {

        Payment paymentToFail = paymentRepository.findById(paymentId).orElseThrow(
                ()-> new RuntimeException("Payment not found")
        );
        paymentToFail.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(paymentToFail);
        PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent();
        paymentFailedEvent.setPaymentId(paymentId);
        paymentFailedEvent.setReason(reason);
        paymentFailedEvent.setFailedAt(LocalDateTime.now());
        paymentFailedEvent.setOrderId(paymentToFail.getOrderId());
        String payload = objectMapper.writeValueAsString(paymentFailedEvent);
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setPublished(false);
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setEventType("PaymentFailedEvent");
        outboxEventRepository.save(outboxEvent);
        return paymentToFail;

    }

}
