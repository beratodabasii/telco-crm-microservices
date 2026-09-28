package com.telcox.productcatalogservice.service;

import com.telcox.productcatalogservice.entity.OutboxEvent;
import com.telcox.productcatalogservice.event.TariffCreatedEvent;
import com.telcox.productcatalogservice.event.TariffPriceChangedEvent;
import com.telcox.productcatalogservice.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    @Scheduled(fixedRate = 5000)
    public void publishPendingEvents(){
        List<OutboxEvent> outboxEvents = outboxEventRepository.findByPublishedFalse();

        for (OutboxEvent outboxEvent : outboxEvents) {
            String eventType = outboxEvent.getEventType();
            String topic = null;
            if (eventType.equals(TariffCreatedEvent.class.getSimpleName())) {
                topic = "tariff.created";
            } else if (eventType.equals(TariffPriceChangedEvent.class.getSimpleName())) {
                topic = "tariff.price-changed";
            }
            if(topic==null){
                continue;
            }
//            var future = kafkaTemplate.send(topic, outboxEvent.getPayload());
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(topic, outboxEvent.getPayload());

            future.whenComplete((result,exception)->{
                if(exception == null){
                    outboxEvent.setPublished(true);
                    outboxEventRepository.save(outboxEvent);
                }else{
                    System.out.println(exception.getMessage());
                }
            });

        }


    }
    
}
