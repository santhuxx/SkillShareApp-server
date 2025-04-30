// ChatController.java
package com.example.skillshare.controller;

import com.example.skillshare.model.ChatSession;
import com.example.skillshare.model.Message;
import com.example.skillshare.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public ResponseEntity<Message> sendMessage(
            @RequestParam String receiverId,
            @RequestParam String content) throws ExecutionException, InterruptedException {

        Message message = chatService.sendMessage(receiverId, content);
        return ResponseEntity.ok(message);
    }

    

    @GetMapping("/messages/{receiverId}")
    public ResponseEntity<List<Message>> getMessagesWithUser(
            @PathVariable String receiverId) throws ExecutionException, InterruptedException {

        List<Message> messages = chatService.getMessagesWithUser(receiverId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> getChatSessions() throws ExecutionException, InterruptedException {
        List<ChatSession> sessions = chatService.getChatSessions();
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/read/{messageId}")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable String messageId) throws ExecutionException, InterruptedException {

        chatService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/message/{messageId}")
    public ResponseEntity<Message> updateMessage(
            @PathVariable String messageId,
            @RequestParam String newContent) throws ExecutionException, InterruptedException {

        Message updatedMessage = chatService.updateMessage(messageId, newContent);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable String messageId) throws ExecutionException, InterruptedException {

        chatService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}