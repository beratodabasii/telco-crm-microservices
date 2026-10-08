package com.telcox.subscriptionservice.service;

import com.telcox.subscriptionservice.dto.CreateSubscriptionRequest;
import com.telcox.subscriptionservice.dto.SubscriptionResponse;
import com.telcox.subscriptionservice.entity.MsisdnPool;
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
    private final MsisdnPoolService msisdnPoolService;

    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        Subscription subscription = new Subscription();
        subscription.setCustomerId(request.getCustomerId());
        subscription.setOrderId(request.getOrderId());
        subscription.setTariffCode(request.getTariffCode());
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setCreatedAt(LocalDateTime.now());
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return mapToResponse(savedSubscription);
    }
    @Transactional
    public SubscriptionResponse activateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(
                () -> new RuntimeException("Subscription not found")
        );

        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            return mapToResponse(subscription);
        }

        MsisdnPool allocatedMsisdn = msisdnPoolService.allocateMsisdn();
        subscription.setMsisdn(allocatedMsisdn.getMsisdn());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());

        SubscriptionActivatedEvent event = new SubscriptionActivatedEvent();
        event.setSubscriptionId(subscription.getId());
        event.setOrderId(subscription.getOrderId());
        event.setCustomerId(subscription.getCustomerId());
        event.setTariffCode(subscription.getTariffCode());
        event.setActivatedAt(LocalDateTime.now());
        event.setMsisdn(subscription.getMsisdn());

        String payload = objectMapper.writeValueAsString(event);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("SubscriptionActivatedEvent");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);
        outboxEventRepository.save(outboxEvent);
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return mapToResponse(savedSubscription);
    }

    @Transactional
    public SubscriptionResponse createSubscriptionFromOrderPaid(OrderPaidEvent event){
        Optional<Subscription> existingSubscription = subscriptionRepository.findByOrderId(event.getOrderId());
        if (existingSubscription.isPresent()) {
            Subscription existing = existingSubscription.get();

            return mapToResponse(existing);
        }
        CreateSubscriptionRequest request = new CreateSubscriptionRequest();
        request.setOrderId(event.getOrderId());
        request.setCustomerId(event.getCustomerId());
        request.setTariffCode(event.getTariffCode());
        SubscriptionResponse createdSubscription = createSubscription(request);
        return activateSubscription(createdSubscription.getId());

    }

    private SubscriptionResponse mapToResponse(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(subscription.getId());
        response.setCustomerId(subscription.getCustomerId());
        response.setOrderId(subscription.getOrderId());
        response.setTariffCode(subscription.getTariffCode());
        response.setStatus(subscription.getStatus());
        response.setStartDate(subscription.getStartDate());
        response.setCreatedAt(subscription.getCreatedAt());
        response.setMsisdn(subscription.getMsisdn());

        return response;
    }
}
