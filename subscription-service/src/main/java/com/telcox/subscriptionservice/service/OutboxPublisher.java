package com.telcox.subscriptionservice.service;

import com.telcox.subscriptionservice.entity.OutboxEvent;
import com.telcox.subscriptionservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 1000)
    public void publishPendingEvents(){
        List<OutboxEvent> events = outboxEventRepository.findByPublishedFalse();
        for(OutboxEvent outboxEvent : events){
            String eventType = outboxEvent.getEventType();
            String topic = null;
            if(eventType.equals("SubscriptionActivatedEvent")){
                topic = "subscription.activated";
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
            });


        }
    }




    }
