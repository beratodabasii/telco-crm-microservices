package com.telcox.orderservice.consumer;

import com.telcox.orderservice.event.SubscriptionActivatedEvent;
import com.telcox.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SubscriptionEventConsumer {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "subscription.activated", groupId = "order-service")
    public void handleSubscriptionActivated(String message) {
        SubscriptionActivatedEvent event = objectMapper.readValue(message ,  SubscriptionActivatedEvent.class );
        orderService.markOrderAsFulfilled(event.getOrderId());


    }
}
