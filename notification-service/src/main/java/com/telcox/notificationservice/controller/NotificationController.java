package com.telcox.notificationservice.controller;

import com.telcox.notificationservice.dto.CreateNotificationRequest;
import com.telcox.notificationservice.dto.NotificationResponse;
import com.telcox.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public NotificationResponse createNotification(@RequestBody CreateNotificationRequest request){
        return notificationService.createNotification(request);
    }

    @PostMapping("{notificationId}/send")
    public NotificationResponse sendNotification(@PathVariable Long notificationId){
        return notificationService.sendNotification(notificationId);
    }

    @GetMapping("/users/{userId}/history")
    public List<NotificationResponse> getNotificationsByUserId(@PathVariable Long userId){
        return notificationService.getNotificationsByUserId(userId);
    }
}
