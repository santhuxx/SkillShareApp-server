package com.example.skillshare.service;

import com.example.skillshare.model.ChatSession;
import com.example.skillshare.model.Message;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ChatService {

    private final FirestoreService firestoreService;

    public ChatService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public Map<String, Object> sendMessage(String receiverId, String content) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String senderId = oAuth2User.getName();

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setTimestamp(new Date());
        message.setRead(false);

        Message savedMessage = firestoreService.createMessage(message);

        ChatSession session = firestoreService.getChatSessionBetweenUsers(senderId, receiverId)
                .orElseGet(() -> {
                    ChatSession newSession = new ChatSession();
                    newSession.setUser1Id(senderId);
                    newSession.setUser2Id(receiverId);
                    newSession.setLastUpdated(new Date());
                    return newSession;
                });

        session.setLastUpdated(new Date());
        firestoreService.updateChatSession(session);

        // Return a Map with message fields and usernames
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", savedMessage.getId());
        messageMap.put("senderId", savedMessage.getSenderId());
        messageMap.put("receiverId", savedMessage.getReceiverId());
        messageMap.put("content", savedMessage.getContent());
        messageMap.put("timestamp", savedMessage.getTimestamp());
        messageMap.put("read", savedMessage.isRead());
        messageMap.put("deleted", savedMessage.isDeleted());
        messageMap.put("updatedAt", savedMessage.getUpdatedAt());
        messageMap.put("senderUsername", firestoreService.getUsernameByUserId(senderId));
        messageMap.put("receiverUsername", firestoreService.getUsernameByUserId(receiverId));

        return messageMap;
    }

    public List<Map<String, Object>> getMessagesWithUser(String otherUserId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        List<Message> messages = firestoreService.getMessagesBetweenUsers(currentUserId, otherUserId);
        List<Map<String, Object>> messagesWithUsernames = new ArrayList<>();

        for (Message message : messages) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("id", message.getId());
            messageMap.put("senderId", message.getSenderId());
            messageMap.put("receiverId", message.getReceiverId());
            messageMap.put("content", message.getContent());
            messageMap.put("timestamp", message.getTimestamp());
            messageMap.put("read", message.isRead());
            messageMap.put("deleted", message.isDeleted());
            messageMap.put("updatedAt", message.getUpdatedAt());
            messageMap.put("senderUsername", firestoreService.getUsernameByUserId(message.getSenderId()));
            messageMap.put("receiverUsername", firestoreService.getUsernameByUserId(message.getReceiverId()));
            messagesWithUsernames.add(messageMap);
        }

        return messagesWithUsernames;
    }

    public List<Map<String, Object>> getChatSessions() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        List<ChatSession> sessions = firestoreService.getChatSessionsForUser(currentUserId);
        List<Map<String, Object>> sessionsWithUsernames = new ArrayList<>();

        for (ChatSession session : sessions) {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("id", session.getId());
            sessionMap.put("user1Id", session.getUser1Id());
            sessionMap.put("user2Id", session.getUser2Id());
            sessionMap.put("lastUpdated", session.getLastUpdated());
            String otherUserId = session.getUser1Id().equals(currentUserId) ? session.getUser2Id() : session.getUser1Id();
            sessionMap.put("otherUserId", otherUserId);
            sessionMap.put("otherUsername", firestoreService.getUsernameByUserId(otherUserId));
            sessionsWithUsernames.add(sessionMap);
        }

        return sessionsWithUsernames;
    }

    public void markMessageAsRead(String messageId) throws ExecutionException, InterruptedException {
        Message message = firestoreService.getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        if (message.getReceiverId().equals(currentUserId)) {
            message.setRead(true);
            firestoreService.updateMessage(messageId, message);
        }
    }

    public Map<String, Object> updateMessage(String messageId, String newContent)
            throws ExecutionException, InterruptedException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        Message message = firestoreService.getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("You can only update your own messages");
        }

        message.setContent(newContent);
        message.setUpdatedAt(new Date());

        Message updatedMessage = firestoreService.updateMessage(messageId, message);

        // Return a Map with updated message fields and usernames
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", updatedMessage.getId());
        messageMap.put("senderId", updatedMessage.getSenderId());
        messageMap.put("receiverId", updatedMessage.getReceiverId());
        messageMap.put("content", updatedMessage.getContent());
        messageMap.put("timestamp", updatedMessage.getTimestamp());
        messageMap.put("read", updatedMessage.isRead());
        messageMap.put("deleted", updatedMessage.isDeleted());
        messageMap.put("updatedAt", updatedMessage.getUpdatedAt());
        messageMap.put("senderUsername", firestoreService.getUsernameByUserId(updatedMessage.getSenderId()));
        messageMap.put("receiverUsername", firestoreService.getUsernameByUserId(updatedMessage.getReceiverId()));

        return messageMap;
    }

    public void deleteMessage(String messageId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        Message message = firestoreService.getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("You can only delete your own messages");
        }

        message.setDeleted(true);
        message.setUpdatedAt(new Date());

        firestoreService.updateMessage(messageId, message);
    }
}