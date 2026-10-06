package com.telcox.orderservice.service;

import com.telcox.orderservice.entity.OutboxEvent;
import com.telcox.orderservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 1000)
    public void publishPendingEvents() {
        List<OutboxEvent> outboxEvents = outboxEventRepository.findByPublishedFalse();
        for (OutboxEvent outboxEvent : outboxEvents) {
            String eventType = outboxEvent.getEventType();
            String topic = null;
            if (eventType.equals("OrderCreatedEvent")) {
                topic = "order.created";
            } else if (eventType.equals("OrderCancelledEvent")) {
                topic = "order.cancelled";
            } else if (eventType.equals("OrderConfirmedEvent")) {
                topic = "order.confirmed";
            } else if (eventType.equals("OrderPaidEvent")) {
                topic = "order.paid";
            }
            if(topic==null){
                continue;
            }

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, outboxEvent.getPayload());
            future.whenComplete((result,exception)->{
                if (exception == null) {
                    outboxEvent.setPublished(true);
                    outboxEventRepository.save(outboxEvent);
                } else  {
                    System.out.println(exception.getMessage());
                }

            } );
        }

    }
}
