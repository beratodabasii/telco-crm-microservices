package com.telcox.paymentservice.service;

import com.telcox.paymentservice.dto.CreatePaymentRequest;
import com.telcox.paymentservice.dto.PaymentResponse;
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

    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }
    @Transactional
    public PaymentResponse completePayment(Long paymentId) {
       Payment paymentToComplete = paymentRepository.findById(paymentId).orElseThrow(
               ()-> new RuntimeException("Payment not found")
       );
        if (paymentToComplete.getStatus() == PaymentStatus.COMPLETED) {
            return mapToResponse(paymentToComplete);
        }
        if (paymentToComplete.getStatus() == PaymentStatus.FAILED) {
            throw new RuntimeException("Failed payment cannot be completed");
        }

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
       return mapToResponse(paymentToComplete);
    }

    @Transactional
    public PaymentResponse failPayment(Long paymentId, String reason) {

        Payment paymentToFail = paymentRepository.findById(paymentId).orElseThrow(
                ()-> new RuntimeException("Payment not found")
        );
        if (paymentToFail.getStatus() == PaymentStatus.FAILED) {
            return mapToResponse(paymentToFail);
        }
        if (paymentToFail.getStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Completed payment cannot be failed");
        }
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
        return mapToResponse(paymentToFail);

    }


    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus());
        response.setCreatedAt(payment.getCreatedAt());

        return response;
    }

}
