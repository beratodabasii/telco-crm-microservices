package com.telcox.subscriptionservice.service;

import com.telcox.subscriptionservice.entity.OutboxEvent;
import com.telcox.subscriptionservice.entity.Subscription;
import com.telcox.subscriptionservice.enums.SubscriptionStatus;
import com.telcox.subscriptionservice.event.OrderPaidEvent;
import com.telcox.subscriptionservice.event.SubscriptionActivatedEvent;
import com.telcox.subscriptionservice.repository.OutboxEventRepository;
import com.telcox.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public Subscription createSubscription(Subscription subscription) {
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setCreatedAt(LocalDateTime.now());
        return subscriptionRepository.save(subscription);
    }
    @Transactional
    public Subscription activateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(
                () -> new RuntimeException("Subscription not found")
        );
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            return subscription;
        }
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());

        SubscriptionActivatedEvent event = new SubscriptionActivatedEvent();
        event.setSubscriptionId(subscription.getId());
        event.setOrderId(subscription.getOrderId());
        event.setCustomerId(subscription.getCustomerId());
        event.setTariffCode(subscription.getTariffCode());
        event.setActivatedAt(LocalDateTime.now());

        String payload = objectMapper.writeValueAsString(event);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("SubscriptionActivatedEvent");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);
        outboxEventRepository.save(outboxEvent);

        return subscriptionRepository.save(subscription);
    }
    @Transactional
    public Subscription createSubscriptionFromOrderPaid(OrderPaidEvent event){
        Optional<Subscription> existingSubscription = subscriptionRepository.findByOrderId(event.getOrderId());
        if(existingSubscription.isPresent()){
            return existingSubscription.get() ;
        }
        Subscription subscription = new Subscription();
        subscription.setOrderId(event.getOrderId());
        subscription.setCustomerId(event.getCustomerId());
        subscription.setTariffCode(event.getTariffCode());
        Subscription createdSubscription = createSubscription(subscription);
        return activateSubscription(createdSubscription.getId());

    }
}
