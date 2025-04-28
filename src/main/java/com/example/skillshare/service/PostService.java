package com.example.skillshare.service;

import com.example.skillshare.model.Post;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PostService {

    private final FirestoreService firestoreService;
    private final FirebaseStorageService firebaseStorageService;

    public PostService(FirestoreService firestoreService, FirebaseStorageService firebaseStorageService) {
        this.firestoreService = firestoreService;
        this.firebaseStorageService = firebaseStorageService;
    }

    public Post createPost(String title, String description,
                           MultipartFile[] images, MultipartFile video)
            throws IOException, ExecutionException, InterruptedException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        // Upload images
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = firebaseStorageService.uploadFile(image, "posts/images");
            imageUrls.add(imageUrl);
        }

        // Upload video
        String videoUrl = firebaseStorageService.uploadFile(video, "posts/videos");

        // Create post
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(title);
        post.setDescription(description);
        post.setImageUrls(imageUrls);
        post.setVideoUrl(videoUrl);

        return firestoreService.createPost(post);
    }

    public Post updatePost(String postId, String title, String description,
                           MultipartFile[] newImages, MultipartFile newVideo)
            throws IOException, ExecutionException, InterruptedException {

        Post post = firestoreService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Update title and description
        if (title != null) post.setTitle(title);
        if (description != null) post.setDescription(description);

        // Handle new images if provided
        if (newImages != null && newImages.length > 0) {
            // Delete old images
            post.getImageUrls().forEach(firebaseStorageService::deleteFile);

            // Upload new images
            List<String> newImageUrls = new ArrayList<>();
            for (MultipartFile image : newImages) {
                String imageUrl = firebaseStorageService.uploadFile(image, "posts/images");
                newImageUrls.add(imageUrl);
            }
            post.setImageUrls(newImageUrls);
        }

        // Handle new video if provided
        if (newVideo != null && !newVideo.isEmpty()) {
            // Delete old video
            firebaseStorageService.deleteFile(post.getVideoUrl());

            // Upload new video
            String videoUrl = firebaseStorageService.uploadFile(newVideo, "posts/videos");
            post.setVideoUrl(videoUrl);
        }

        return firestoreService.updatePost(postId, post);
    }

    public void deletePost(String postId) throws ExecutionException, InterruptedException, IOException {
        Post post = firestoreService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Delete all media files
        post.getImageUrls().forEach(firebaseStorageService::deleteFile);
        firebaseStorageService.deleteFile(post.getVideoUrl());

        // Delete post from Firestore
        firestoreService.deletePost(postId);
    }

    public List<Post> getUserPosts() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        return firestoreService.getUserPosts(userId);
    }
}