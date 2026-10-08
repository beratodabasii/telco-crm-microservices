package com.telcox.notificationservice.consumer;

import com.telcox.notificationservice.dto.CreateNotificationRequest;
import com.telcox.notificationservice.dto.NotificationResponse;
import com.telcox.notificationservice.enums.NotificationChannel;
import com.telcox.notificationservice.event.SubscriptionActivatedEvent;
import com.telcox.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class SubscriptionEventConsumer {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "subscription.activated",
            groupId = "notification-service"
    )
    public void consumeSubscriptionActivated(String message) {

        SubscriptionActivatedEvent event =
                objectMapper.readValue(
                        message,
                        SubscriptionActivatedEvent.class
                );
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(event.getCustomerId());
        request.setTemplateCode("WELCOME_SMS");
        request.setChannel(NotificationChannel.SMS);
        request.setPayloadJson(
                "{\"msisdn\":\"" + event.getMsisdn() +
                        "\",\"message\":\"TelcoX'e hoş geldiniz\"}"
        );
        NotificationResponse createdNotification =
                notificationService.createNotification(request);
        notificationService.sendNotification(createdNotification.getId());
    }

}
