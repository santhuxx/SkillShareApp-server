package com.example.skillshare.service;

import com.example.skillshare.model.Notification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

    private final FirestoreService firestoreService;

    public NotificationService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public List<Notification> getUserNotifications() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        System.out.println("Principal: " + principal);
        if (!(principal instanceof OAuth2User)) {
            throw new RuntimeException("Invalid user authentication type: " + principal.getClass().getName());
        }

        OAuth2User oAuth2User = (OAuth2User) principal;
        String userId = oAuth2User.getName();
        System.out.println("User ID: " + userId);
        if (userId == null) {
            throw new RuntimeException("User ID not found in authentication");
        }

        return firestoreService.getNotificationsByUserId(userId);
    }

    public Notification markNotificationAsRead(String notificationId)
            throws ExecutionException, InterruptedException {
        Notification notification = firestoreService.getNotificationById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OAuth2User)) {
            throw new RuntimeException("Invalid user authentication type: " + principal.getClass().getName());
        }

        OAuth2User oAuth2User = (OAuth2User) principal;
        String userId = oAuth2User.getName();
        if (userId == null) {
            throw new RuntimeException("User ID not found in authentication");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this notification");
        }

        notification.setRead(true);
        return firestoreService.updateNotification(notificationId, notification);
    }
}

