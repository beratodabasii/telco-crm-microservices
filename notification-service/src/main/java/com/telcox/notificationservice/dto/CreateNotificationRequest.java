package com.telcox.notificationservice.dto;

import com.telcox.notificationservice.enums.NotificationChannel;
import lombok.Data;

@Data
public class CreateNotificationRequest {

    private Long userId;
    private String templateCode;
    private NotificationChannel channel;
    private String payloadJson;
}