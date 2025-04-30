// ChatService.java
package com.example.skillshare.service;

import com.example.skillshare.model.ChatSession;
import com.example.skillshare.model.Message;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ChatService {

    private final FirestoreService firestoreService;

    public ChatService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public Message sendMessage(String receiverId, String content) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String senderId = oAuth2User.getName();

        // Create message
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setTimestamp(new Date());
        message.setRead(false);

        // Save message
        Message savedMessage = firestoreService.createMessage(message);

        // Get or create chat session
        ChatSession session = firestoreService.getChatSessionBetweenUsers(senderId, receiverId)
                .orElseGet(() -> {
                    ChatSession newSession = new ChatSession();
                    newSession.setUser1Id(senderId);
                    newSession.setUser2Id(receiverId);
                    newSession.setLastUpdated(new Date());
                    return newSession;
                });

        // Update session
        session.setLastUpdated(new Date());
        firestoreService.updateChatSession(session);

        return savedMessage;
    }

    public List<Message> getMessagesWithUser(String otherUserId) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        return firestoreService.getMessagesBetweenUsers(currentUserId, otherUserId);
    }

    public List<ChatSession> getChatSessions() throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        return firestoreService.getChatSessionsForUser(currentUserId);
    }

    public void markMessageAsRead(String messageId) throws ExecutionException, InterruptedException {
        Message message = firestoreService.getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        // Only mark as read if current user is the receiver
        if (message.getReceiverId().equals(currentUserId)) {
            message.setRead(true);
            firestoreService.updateMessage(messageId, message);
        }
    }

    public Message updateMessage(String messageId, String newContent)
            throws ExecutionException, InterruptedException {

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        // Get message
        Message message = firestoreService.getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // Check if current user is the sender
        if (!message.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("You can only update your own messages");
        }

        // Update message
        message.setContent(newContent);
        message.setUpdatedAt(new Date());

        // Return the updated message
        return firestoreService.updateMessage(messageId, message);
    }

    public void deleteMessage(String messageId) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        // Get message
        Message message = firestoreService.getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // Check if current user is the sender
        if (!message.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("You can only delete your own messages");
        }

        // Soft delete the message
        message.setDeleted(true);
        message.setUpdatedAt(new Date());

        firestoreService.updateMessage(messageId, message);
    }
}