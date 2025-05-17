package com.example.skillshare.service;

import com.example.skillshare.model.*;
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

    // ... Existing methods (Post, LearningPlan, Comment, Like, Message, ChatSession) ...

    public String getUsernameByUserId(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .limit(1)
                .get();

        QuerySnapshot querySnapshot = future.get();
        if (!querySnapshot.isEmpty()) {
            Post post = querySnapshot.getDocuments().get(0).toObject(Post.class);
            return post.getUsername();
        }
        return null; // Return null if no post found; handle in service layer
    }

    public Post createPost(Post post) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        post.setId(id);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());

        ApiFuture<WriteResult> future = firestore.collection("posts")
                .document(id)
                .set(post);

        future.get();
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

    public LearningPlan createLearningPlan(LearningPlan learningPlan) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        learningPlan.setId(id);
        learningPlan.setCreatedAt(new Date());
        learningPlan.setUpdatedAt(new Date());

        ApiFuture<WriteResult> future = firestore.collection("learningPlans")
                .document(id)
                .set(learningPlan);

        future.get();
        return learningPlan;
    }

    public LearningPlan updateLearningPlan(String planId, LearningPlan learningPlan) throws ExecutionException, InterruptedException {
        learningPlan.setUpdatedAt(new Date());

        ApiFuture<WriteResult> future = firestore.collection("learningPlans")
                .document(planId)
                .set(learningPlan, SetOptions.merge());

        future.get();
        return learningPlan;
    }

    public void deleteLearningPlan(String planId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection("learningPlans")
                .document(planId)
                .delete();

        future.get();
    }

    public List<LearningPlan> getUserLearningPlans(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("learningPlans")
                .whereEqualTo("userId", userId)
                .get();

        QuerySnapshot querySnapshot = future.get();
        List<LearningPlan> plans = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            plans.add(document.toObject(LearningPlan.class));
        }

        return plans;
    }

    public Optional<LearningPlan> getLearningPlanById(String planId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("learningPlans").document(planId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(LearningPlan.class));
        } else {
            return Optional.empty();
        }
    }

    public List<LearningPlan> getAllLearningPlans() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("learningPlans").get();
        QuerySnapshot querySnapshot = future.get();
        List<LearningPlan> plans = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            plans.add(document.toObject(LearningPlan.class));
        }

        return plans;
    }

    public Comment createComment(Comment comment) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        comment.setId(id);

        ApiFuture<WriteResult> future = firestore.collection("comments")
                .document(id)
                .set(comment);

        future.get();
        return comment;
    }

    public Comment updateComment(String commentId, Comment comment) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection("comments")
                .document(commentId)
                .set(comment, SetOptions.merge());

        future.get();
        return comment;
    }

    public void deleteComment(String commentId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection("comments")
                .document(commentId)
                .delete();

        future.get();
    }

    public Optional<Comment> getCommentById(String commentId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("comments").document(commentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Comment.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Comment> getCommentsByPostId(String postId) throws ExecutionException, InterruptedException {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection("comments")
                    .whereEqualTo("postId", postId)
                    .orderBy("createdAt", Query.Direction.ASCENDING)
                    .get();

            QuerySnapshot querySnapshot = future.get();
            List<Comment> comments = new ArrayList<>();

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                comments.add(document.toObject(Comment.class));
            }

            return comments;
        } catch (Exception e) {
            System.err.println("Error fetching comments: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Like createLike(Like like) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        like.setId(id);

        ApiFuture<WriteResult> future = firestore.collection("likes")
                .document(id)
                .set(like);

        future.get();
        return like;
    }

    public void deleteLike(String likeId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection("likes")
                .document(likeId)
                .delete();

        future.get();
    }

    public Optional<Like> getLikeByPostIdAndUserId(String postId, String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("likes")
                .whereEqualTo("postId", postId)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get();

        QuerySnapshot querySnapshot = future.get();

        if (!querySnapshot.isEmpty()) {
            return Optional.of(querySnapshot.getDocuments().get(0).toObject(Like.class));
        } else {
            return Optional.empty();
        }
    }

    public int getLikeCountByPostId(String postId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("likes")
                .whereEqualTo("postId", postId)
                .get();

        QuerySnapshot querySnapshot = future.get();
        return querySnapshot.size();
    }

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
        Query query1 = firestore.collection("messages")
                .whereEqualTo("senderId", user1Id)
                .whereEqualTo("receiverId", user2Id)
                .whereEqualTo("deleted", false);

        Query query2 = firestore.collection("messages")
                .whereEqualTo("senderId", user2Id)
                .whereEqualTo("receiverId", user1Id)
                .whereEqualTo("deleted", false);

        ApiFuture<QuerySnapshot> future1 = query1.get();
        ApiFuture<QuerySnapshot> future2 = query2.get();

        List<Message> messages = new ArrayList<>();

        QuerySnapshot snapshot1 = future1.get();
        for (DocumentSnapshot document : snapshot1.getDocuments()) {
            messages.add(document.toObject(Message.class));
        }

        QuerySnapshot snapshot2 = future2.get();
        for (DocumentSnapshot document : snapshot2.getDocuments()) {
            messages.add(document.toObject(Message.class));
        }

        messages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        return messages;
    }


    public Optional<ChatSession> getChatSessionBetweenUsers(String user1Id, String user2Id) throws ExecutionException, InterruptedException {
        Query query1 = firestore.collection("chat_sessions")
                .whereEqualTo("user1Id", user1Id)
                .whereEqualTo("user2Id", user2Id);

        Query query2 = firestore.collection("chat_sessions")
                .whereEqualTo("user1Id", user2Id)
                .whereEqualTo("user2Id", user1Id);

        ApiFuture<QuerySnapshot> future1 = query1.get();
        QuerySnapshot snapshot1 = future1.get();

        if (!snapshot1.isEmpty()) {
            return Optional.of(snapshot1.getDocuments().get(0).toObject(ChatSession.class));
        }

        ApiFuture<QuerySnapshot> future2 = query2.get();
        QuerySnapshot snapshot2 = future2.get();

        if (!snapshot2.isEmpty()) {
            return Optional.of(snapshot2.getDocuments().get(0).toObject(ChatSession.class));
        }

        return Optional.empty();
    }

    public List<ChatSession> getChatSessionsForUser(String userId) throws ExecutionException, InterruptedException {
        Query query1 = firestore.collection("chat_sessions")
                .whereEqualTo("user1Id", userId);

        Query query2 = firestore.collection("chat_sessions")
                .whereEqualTo("user2Id", userId);

        ApiFuture<QuerySnapshot> future1 = query1.get();
        ApiFuture<QuerySnapshot> future2 = query2.get();

        List<ChatSession> sessions = new ArrayList<>();

        QuerySnapshot snapshot1 = future1.get();
        for (DocumentSnapshot document : snapshot1.getDocuments()) {
            sessions.add(document.toObject(ChatSession.class));
        }

        QuerySnapshot snapshot2 = future2.get();
        for (DocumentSnapshot document : snapshot2.getDocuments()) {
            sessions.add(document.toObject(ChatSession.class));
        }

        sessions.sort((s1, s2) -> s2.getLastUpdated().compareTo(s1.getLastUpdated()));

        return sessions;
    }

    public void updateChatSession(ChatSession session) throws ExecutionException, InterruptedException {
        if (session.getId() == null) {
            DocumentReference docRef = firestore.collection("chat_sessions").document();
            session.setId(docRef.getId());
            docRef.set(session).get();
        } else {
            firestore.collection("chat_sessions").document(session.getId()).set(session).get();
        }
    }

    public Message updateMessage(String messageId, Message message) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("messages").document(messageId);
        ApiFuture<WriteResult> result = docRef.set(message);
        result.get();
        return message;
    }

    public Notification createNotification(Notification notification) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        notification.setId(id);
        firestore.collection("notifications").document(id).set(notification).get();
        return notification;
    }

    public List<Notification> getNotificationsByUserId(String userId) throws ExecutionException, InterruptedException {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        System.out.println("Fetching notifications for user: " + userId);
        try {
            QuerySnapshot querySnapshot = firestore.collection("notifications")
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", com.google.cloud.firestore.Query.Direction.DESCENDING)
                    .get()
                    .get();
            List<Notification> notifications = new ArrayList<>();
            querySnapshot.getDocuments().forEach(doc -> notifications.add(doc.toObject(Notification.class)));
            System.out.println("Found " + notifications.size() + " notifications");
            return notifications;
        } catch (Exception e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
            throw e;
        }
    }

    public Notification updateNotification(String notificationId, Notification notification)
            throws ExecutionException, InterruptedException {
        notification.setId(notificationId);
        firestore.collection("notifications").document(notificationId).set(notification).get();
        return notification;
    }

    public Optional<Notification> getNotificationById(String notificationId)
            throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection("notifications").document(notificationId).get().get();
        return document.exists() ? Optional.of(document.toObject(Notification.class)) : Optional.empty();
    }
}