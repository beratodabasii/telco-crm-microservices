package com.telcox.usageservice.publisher;

import com.telcox.usageservice.entity.OutboxEvent;
import com.telcox.usageservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository.findByPublishedFalse();

        for (OutboxEvent event : events) {

            kafkaTemplate.send(
                    "usage.threshold.reached",
                    event.getPayload()
            );

            event.setPublished(true);
            outboxEventRepository.save(event);
        }
    }
}
