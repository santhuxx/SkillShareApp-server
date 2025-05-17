package com.example.skillshare.controller;

import com.example.skillshare.model.Notification;
import com.example.skillshare.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications()
            throws ExecutionException, InterruptedException {
        try {
            List<Notification> notifications = notificationService.getUserNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch notifications: " + e.getMessage());
        }
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markNotificationAsRead(
            @PathVariable String notificationId) throws ExecutionException, InterruptedException {
        Notification notification = notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }
}

