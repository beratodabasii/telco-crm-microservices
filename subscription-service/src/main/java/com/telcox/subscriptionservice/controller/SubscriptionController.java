package com.telcox.subscriptionservice.controller;

import com.telcox.subscriptionservice.entity.Subscription;
import com.telcox.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription) {
        return subscriptionService.createSubscription(subscription);
    }

    @PostMapping("/{subscriptionId}/activate")
    public Subscription activateSubscription(@PathVariable Long subscriptionId) {
        return subscriptionService.activateSubscription(subscriptionId);
    }

}
