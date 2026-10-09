package com.telcox.subscriptionservice.controller;

import com.telcox.subscriptionservice.dto.CreateSubscriptionRequest;
import com.telcox.subscriptionservice.dto.SubscriptionResponse;
import com.telcox.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public SubscriptionResponse createSubscription(@RequestBody CreateSubscriptionRequest request) {
        return subscriptionService.createSubscription(request);
    }

    @PostMapping("/{subscriptionId}/activate")
    public SubscriptionResponse activateSubscription(@PathVariable Long subscriptionId) {
        return subscriptionService.activateSubscription(subscriptionId);
    }

    @GetMapping("/{subscriptionId}")
    public SubscriptionResponse getSubscriptionById(@PathVariable Long subscriptionId) {
        return subscriptionService.getSubscriptionById(subscriptionId);
    }

}
