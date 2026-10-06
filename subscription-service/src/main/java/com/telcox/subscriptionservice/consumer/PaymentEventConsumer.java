package com.telcox.subscriptionservice.consumer;

import com.telcox.subscriptionservice.event.OrderPaidEvent;
import com.telcox.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;
    private final SubscriptionService subscriptionService;


    @KafkaListener(topics = "order.paid", groupId = "subscription-service")
    public void handleOrderPaid(String message) {
        OrderPaidEvent event = objectMapper.readValue(message, OrderPaidEvent.class);

        subscriptionService.createSubscriptionFromOrderPaid(event);


    }

}
