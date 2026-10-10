package com.telcox.notificationservice.consumer;

import com.telcox.notificationservice.client.SubscriptionClient;
import com.telcox.notificationservice.dto.CreateNotificationRequest;
import com.telcox.notificationservice.dto.NotificationResponse;
import com.telcox.notificationservice.dto.SubscriptionClientResponse;
import com.telcox.notificationservice.enums.NotificationChannel;
import com.telcox.notificationservice.event.UsageThresholdReachedEvent;
import com.telcox.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class UsageThresholdConsumer {
    private final ObjectMapper objectMapper;
    private final SubscriptionClient subscriptionClient;
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "usage.threshold.reached",
            groupId = "notification-service"
    )
    public void consume(String message) {

        UsageThresholdReachedEvent event =
                objectMapper.readValue(
                        message,
                        UsageThresholdReachedEvent.class
                );

        System.out.println(
                "Threshold event parsed: "
                        + event.getSubscriptionId()
                        + " "
                        + event.getUsageType()
                        + " "
                        + event.getThresholdLevel()
                        + " "
                        + event.getPercentage()
        );
        SubscriptionClientResponse subscription =
                subscriptionClient.getSubscriptionById(event.getSubscriptionId());
        System.out.println(
                "Threshold notification customerId: "
                        + subscription.getCustomerId()
        );
        CreateNotificationRequest request = new CreateNotificationRequest();

        request.setUserId(subscription.getCustomerId());
        request.setTemplateCode("USAGE_THRESHOLD");
        request.setChannel(NotificationChannel.SMS);
        request.setPayloadJson(
                "{\"usageType\":\"" + event.getUsageType()
                        + "\",\"thresholdLevel\":\"" + event.getThresholdLevel()
                        + "\",\"percentage\":" + event.getPercentage()
                        + "}"
        );
        NotificationResponse notification =
                notificationService.createNotification(request);
        NotificationResponse sentNotification =
                notificationService.sendNotification(notification.getId());
        System.out.println(
                "Threshold notification sent: "
                        + sentNotification.getId()
                        + " "
                        + sentNotification.getStatus()
        );
    }
}
