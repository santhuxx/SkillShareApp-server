package com.example.skillshare.service;

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
}