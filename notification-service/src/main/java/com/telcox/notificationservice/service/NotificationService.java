package com.telcox.notificationservice.service;

import com.telcox.notificationservice.dto.CreateNotificationRequest;
import com.telcox.notificationservice.dto.NotificationResponse;
import com.telcox.notificationservice.entity.Notification;
import com.telcox.notificationservice.enums.NotificationStatus;
import com.telcox.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationResponse createNotification(CreateNotificationRequest request){
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTemplateCode(request.getTemplateCode());
        notification.setChannel(request.getChannel());
        notification.setPayloadJson(request.getPayloadJson());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());
        Notification savedNotification =notificationRepository.save(notification);

        return mapToResponse(savedNotification);

    }

    public NotificationResponse sendNotification(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).
                orElseThrow(()-> new RuntimeException("Notification not found"));
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        Notification savedNotification =
                notificationRepository.save(notification);
        return mapToResponse(savedNotification);
    }

    public List<NotificationResponse> getNotificationsByUserId(Long userId){

        List<Notification>notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToResponse)
                .toList();


    }

    private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse notificationResponse = new NotificationResponse();

        notificationResponse.setId(notification.getId());
        notificationResponse.setUserId(notification.getUserId());
        notificationResponse.setTemplateCode(notification.getTemplateCode());
        notificationResponse.setChannel(notification.getChannel());
        notificationResponse.setPayloadJson(notification.getPayloadJson());
        notificationResponse.setStatus(notification.getStatus());
        notificationResponse.setCreatedAt(notification.getCreatedAt());
        notificationResponse.setSentAt(notification.getSentAt());

        return notificationResponse;
    }
}
