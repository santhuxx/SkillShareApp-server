package com.example.skillshare.service;

import com.example.skillshare.model.ChatSession;
import com.example.skillshare.model.Message;
import com.example.skillshare.model.Post;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    private final Firestore firestore;

    public FirestoreService(Firestore firestore) {
        this.firestore = firestore;
    }

    public Post createPost(Post post) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        post.setId(id);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());

        ApiFuture<WriteResult> future = firestore.collection("posts")
                .document(id)
                .set(post);

        future.get(); // Wait for the operation to complete
        return post;
    }

    public Post updatePost(String postId, Post post) throws ExecutionException, InterruptedException {
        post.setUpdatedAt(new Date());

        ApiFuture<WriteResult> future = firestore.collection("posts")
                .document(postId)
                .set(post, SetOptions.merge());

        future.get();
        return post;
    }

    public void deletePost(String postId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection("posts")
                .document(postId)
                .delete();

        future.get();
    }

    public List<Post> getUserPosts(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get();

        QuerySnapshot querySnapshot = future.get();
        List<Post> posts = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            posts.add(document.toObject(Post.class));
        }

        return posts;
    }

    public Optional<Post> getPostById(String postId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("posts").document(postId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Post.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Post> getAllPosts() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("posts").get();
        QuerySnapshot querySnapshot = future.get();
        List<Post> posts = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            posts.add(document.toObject(Post.class));
        }

        return posts;
    }

    //---------sadee-----------

// Add these methods to FirestoreService.java


    public Message createMessage(Message message) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("messages").document();
        message.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(message);
        result.get();
        return message;
    }

    public Optional<Message> getMessageById(String messageId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("messages").document(messageId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Message.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Message> getMessagesBetweenUsers(String user1Id, String user2Id) throws ExecutionException, InterruptedException {
        // Query for messages where user1 is sender and user2 is receiver

        Query query1 = firestore.collection("messages")
                .whereEqualTo("senderId", user1Id)
                .whereEqualTo("receiverId", user2Id)
                .whereEqualTo("deleted", false);  // Add this filter

        // Query for messages where user2 is sender and user1 is receiver
        Query query2 = firestore.collection("messages")
                .whereEqualTo("senderId", user2Id)
                .whereEqualTo("receiverId", user1Id)
                .whereEqualTo("deleted", false);  // Add this filter

        // Execute both queries
        ApiFuture<QuerySnapshot> future1 = query1.get();
        ApiFuture<QuerySnapshot> future2 = query2.get();

        List<Message> messages = new ArrayList<>();

        // Process first query results
        for (DocumentSnapshot document : future1.get().getDocuments()) {
            messages.add(document.toObject(Message.class));
        }

        // Process second query results
        for (DocumentSnapshot document : future2.get().getDocuments()) {
            messages.add(document.toObject(Message.class));
        }

        // Sort messages by timestamp
        messages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        return messages;
    }

    public Optional<ChatSession> getChatSessionBetweenUsers(String user1Id, String user2Id) throws ExecutionException, InterruptedException {
        // Query for session where user1 is user1 and user2 is user2
        Query query1 = firestore.collection("chat_sessions")
                .whereEqualTo("user1Id", user1Id)
                .whereEqualTo("user2Id", user2Id);

        // Query for session where user1 is user2 and user2 is user1
        Query query2 = firestore.collection("chat_sessions")
                .whereEqualTo("user1Id", user2Id)
                .whereEqualTo("user2Id", user1Id);

        // Execute first query
        ApiFuture<QuerySnapshot> future1 = query1.get();
        QuerySnapshot snapshot1 = future1.get();

        if (!snapshot1.isEmpty()) {
            return Optional.of(snapshot1.getDocuments().get(0).toObject(ChatSession.class));
        }

        // Execute second query
        ApiFuture<QuerySnapshot> future2 = query2.get();
        QuerySnapshot snapshot2 = future2.get();

        if (!snapshot2.isEmpty()) {
            return Optional.of(snapshot2.getDocuments().get(0).toObject(ChatSession.class));
        }

        return Optional.empty();
    }

    public List<ChatSession> getChatSessionsForUser(String userId) throws ExecutionException, InterruptedException {
        // Query for sessions where user is user1
        Query query1 = firestore.collection("chat_sessions")
                .whereEqualTo("user1Id", userId);

        // Query for sessions where user is user2
        Query query2 = firestore.collection("chat_sessions")
                .whereEqualTo("user2Id", userId);

        // Execute both queries
        ApiFuture<QuerySnapshot> future1 = query1.get();
        ApiFuture<QuerySnapshot> future2 = query2.get();

        List<ChatSession> sessions = new ArrayList<>();

        // Process first query results
        for (DocumentSnapshot document : future1.get().getDocuments()) {
            sessions.add(document.toObject(ChatSession.class));
        }

        // Process second query results
        for (DocumentSnapshot document : future2.get().getDocuments()) {
            sessions.add(document.toObject(ChatSession.class));
        }

        // Sort sessions by lastUpdated
        sessions.sort((s1, s2) -> s2.getLastUpdated().compareTo(s1.getLastUpdated()));

        return sessions;
    }

    public void updateChatSession(ChatSession session) throws ExecutionException, InterruptedException {
        if (session.getId() == null) {
            // Create new session
            DocumentReference docRef = firestore.collection("chat_sessions").document();
            session.setId(docRef.getId());
            docRef.set(session).get();
        } else {
            // Update existing session
            firestore.collection("chat_sessions").document(session.getId()).set(session).get();
        }
    }

    public Message updateMessage(String messageId, Message message) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("messages").document(messageId);
        ApiFuture<WriteResult> result = docRef.set(message);
        result.get(); // Wait for the operation to complete
        return message; // Return the updated message
    }



    //--------sadee-------
}