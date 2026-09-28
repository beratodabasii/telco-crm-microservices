package com.telcox.customerservice.service;

import com.telcox.customerservice.event.CustomerKYCApprovedEvent;
import com.telcox.customerservice.event.CustomerRegisteredEvent;
import com.telcox.customerservice.event.CustomerUpdatedEvent;
import com.telcox.customerservice.outbox.OutboxEvent;
import com.telcox.customerservice.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String,String> kafkaTemplate;
    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    @Scheduled(fixedRate = 5000)
    public void publishPendingEvents(){
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByPublishedFalse();
        for (OutboxEvent outboxEvent : pendingEvents) {
            String eventType = outboxEvent.getEventType();
            String topic = null;
            if(eventType.equals(CustomerRegisteredEvent.class.getSimpleName())){
                topic = "customer.registered";

            } else if(eventType.equals(CustomerKYCApprovedEvent.class.getSimpleName())){
                topic = "customer.kyc-approved";
            } else if (eventType.equals(CustomerUpdatedEvent.class.getSimpleName())) {
                topic = "customer.updated";

            }

            CompletableFuture<SendResult<String,String>> future =
                    kafkaTemplate.send(topic, outboxEvent.getPayload());
            future.whenComplete((result, exception) -> {
                if(exception == null){
                    outboxEvent.setPublished(true);
                    outboxEventRepository.save(outboxEvent);
                }else  {
                    System.out.println(exception.getMessage());
                }
            });
        }
    }



}
