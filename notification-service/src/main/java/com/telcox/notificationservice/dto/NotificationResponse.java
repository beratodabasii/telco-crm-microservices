package com.telcox.notificationservice.dto;

import com.telcox.notificationservice.enums.NotificationChannel;
import com.telcox.notificationservice.enums.NotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String templateCode;
    private NotificationChannel channel;
    private String payloadJson;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
