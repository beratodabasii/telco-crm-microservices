package com.telcox.notificationservice.consumer;

import com.telcox.notificationservice.client.SubscriptionClient;
import com.telcox.notificationservice.dto.CreateNotificationRequest;
import com.telcox.notificationservice.dto.NotificationResponse;
import com.telcox.notificationservice.dto.SubscriptionClientResponse;
import com.telcox.notificationservice.enums.NotificationChannel;
import com.telcox.notificationservice.event.InvoiceIssuedEvent;
import com.telcox.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class InvoiceIssuedConsumer {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final SubscriptionClient subscriptionClient;

    @KafkaListener( topics = "billing.invoice.issued",  groupId = "notification-service")
    public void consume(String message) {

        InvoiceIssuedEvent event =
                objectMapper.readValue(message, InvoiceIssuedEvent.class);


        System.out.println(
                "Invoice event parsed: "
                        + event.getInvoiceId()
                        + " "
                        + event.getSubscriptionId()
                        + " "
                        + event.getAmount()
        );

        SubscriptionClientResponse subscription =
                subscriptionClient.getSubscriptionById(event.getSubscriptionId());

        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(subscription.getCustomerId());
        request.setTemplateCode("INVOICE_ISSUED");
        request.setChannel(NotificationChannel.SMS);
        request.setPayloadJson(
                "{\"invoiceId\":" + event.getInvoiceId()
                        + ",\"amount\":" + event.getAmount()
                        + "}"
        );

        NotificationResponse notification =
                notificationService.createNotification(request);

        notificationService.sendNotification(notification.getId());


        System.out.println(
                "Invoice notification customerId: "
                        + subscription.getCustomerId()
        );

    }

}
