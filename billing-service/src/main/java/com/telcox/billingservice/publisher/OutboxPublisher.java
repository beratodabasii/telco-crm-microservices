package com.telcox.billingservice.publisher;

import com.telcox.billingservice.entity.OutboxEvent;
import com.telcox.billingservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events = outboxEventRepository.findByPublishedFalse();

        for (OutboxEvent event : events) {

            String topic;

            if (event.getEventType().equals("InvoiceIssued")) {
                topic = "billing.invoice.issued";
            } else if (event.getEventType().equals("InvoicePaid")) {
                topic = "billing.invoice.paid";
            } else {
                throw new RuntimeException(
                        "Unknown event type: " + event.getEventType()
                );
            }

            kafkaTemplate.send(topic, event.getPayload());

            event.setPublished(true);
            outboxEventRepository.save(event);
        }
    }

}
