package com.telcox.usageservice.client;

import com.telcox.usageservice.dto.SubscriptionClientResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

@Component
public class SubscriptionClient {
    private final RestClient restClient = RestClient.create("http://localhost:9005");


    public SubscriptionClientResponse getSubscriptionById(Long subscriptionId){
        return restClient.get()
                .uri("/api/v1/subscriptions/{subscriptionId}", subscriptionId)
                .retrieve()
                .body(SubscriptionClientResponse.class);
    }

}
