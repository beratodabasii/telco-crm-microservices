package com.telcox.notificationservice.client;

import com.telcox.notificationservice.dto.SubscriptionClientResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SubscriptionClient {

    private final RestClient restClient =
            RestClient.create("http://localhost:9005");

    public SubscriptionClientResponse getSubscriptionById(Long subscriptionId) {
        return restClient.get()
                .uri("/api/v1/subscriptions/{subscriptionId}", subscriptionId)
                .retrieve()
                .body(SubscriptionClientResponse.class);
    }
}