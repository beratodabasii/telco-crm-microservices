package com.telcox.orderservice.consumer;

import com.telcox.orderservice.event.PaymentCompletedEvent;
import com.telcox.orderservice.event.PaymentFailedEvent;
import com.telcox.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "payment.completed", groupId = "order-service")
    public void handlePaymentCompleted(String message) {

        PaymentCompletedEvent event =
                objectMapper.readValue(message, PaymentCompletedEvent.class);
        orderService.markOrderAsPaid(event.getOrderId());


    }

    @KafkaListener(topics = "payment.failed", groupId = "order-service")
    public void handlePaymentFailed(String message) {
        PaymentFailedEvent event =
                objectMapper.readValue(message, PaymentFailedEvent.class);
        System.out.println("Payment failed for order: " + event.getOrderId()
                + " reason: " + event.getReason());


    }

}
