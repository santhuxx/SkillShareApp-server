package com.example.skillshare.controller;

import com.example.skillshare.service.ChatService;
import com.example.skillshare.service.FirestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private final ChatService chatService;

    @Autowired
    private final FirestoreService firestoreService;

    public ChatController(ChatService chatService, FirestoreService firestoreService) {
        this.chatService = chatService;
        this.firestoreService = firestoreService;
    }

    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam String receiverId,
            @RequestParam String content) throws ExecutionException, InterruptedException {

        Map<String, Object> message = chatService.sendMessage(receiverId, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/messages/{receiverId}")
    public ResponseEntity<List<Map<String, Object>>> getMessagesWithUser(
            @PathVariable String receiverId) throws ExecutionException, InterruptedException {

        List<Map<String, Object>> messages = chatService.getMessagesWithUser(receiverId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getChatSessions() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String currentUserId = oAuth2User.getName();

        List<Map<String, Object>> sessions = chatService.getChatSessions();
        Map<String, Object> response = new HashMap<>();
        response.put("currentUserId", currentUserId);
        response.put("sessions", sessions);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read/{messageId}")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable String messageId) throws ExecutionException, InterruptedException {

        chatService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/message/{messageId}")
    public ResponseEntity<Map<String, Object>> updateMessage(
            @PathVariable String messageId,
            @RequestParam String newContent) throws ExecutionException, InterruptedException {

        Map<String, Object> updatedMessage = chatService.updateMessage(messageId, newContent);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable String messageId) throws ExecutionException, InterruptedException {

        chatService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{userId}")
    public ResponseEntity<Map<String, String>> getUsernameByUserId(
            @PathVariable String userId) throws ExecutionException, InterruptedException {

        String username = firestoreService.getUsernameByUserId(userId);
        Map<String, String> response = new HashMap<>();
        response.put("username", username != null ? username : userId);
        return ResponseEntity.ok(response);
    }
}