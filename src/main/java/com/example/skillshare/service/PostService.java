package com.example.skillshare.service;

import com.example.skillshare.model.Post;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PostService {

    private final FirestoreService firestoreService;
    private final FirebaseStorageService firebaseStorageService;
    private final ObjectMapper objectMapper;

    public PostService(FirestoreService firestoreService, FirebaseStorageService firebaseStorageService) {
        this.firestoreService = firestoreService;
        this.firebaseStorageService = firebaseStorageService;
        this.objectMapper = new ObjectMapper();
    }

    public Post createPost(String title, String description,
                           MultipartFile[] images, MultipartFile video)
            throws IOException, ExecutionException, InterruptedException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();
        String username = oAuth2User.getAttribute("name");

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
        post.setUsername(username);
        post.setTitle(title);
        post.setDescription(description);
        post.setImageUrls(imageUrls);
        post.setVideoUrl(videoUrl);

        return firestoreService.createPost(post);
    }

    public Post updatePost(String postId, String title, String description,
                           MultipartFile[] newImages, MultipartFile newVideo, String currentImageUrls)
            throws IOException, ExecutionException, InterruptedException {

        Post post = firestoreService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getAttribute("name");

        // Update title and description
        if (title != null && !title.isEmpty()) post.setTitle(title);
        if (description != null && !description.isEmpty()) post.setDescription(description);
        post.setUsername(username);

        // Handle images
        List<String> updatedImageUrls = new ArrayList<>();
        // Parse currentImageUrls (JSON string) into a List<String>
        List<String> imagesToKeep = new ArrayList<>();
        if (currentImageUrls != null && !currentImageUrls.isEmpty()) {
            try {
                imagesToKeep = objectMapper.readValue(currentImageUrls, new TypeReference<List<String>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse currentImageUrls", e);
            }
        }

        // Delete images that are no longer in currentImageUrls
        List<String> existingImageUrls = post.getImageUrls() != null ? post.getImageUrls() : new ArrayList<>();
        for (String existingUrl : existingImageUrls) {
            if (!imagesToKeep.contains(existingUrl)) {
                try {
                    firebaseStorageService.deleteFile(existingUrl);
                } catch (Exception e) {
                    System.err.println("Failed to delete image: " + existingUrl + ". Continuing...");
                }
            }
        }

        // Add URLs of images to keep
        updatedImageUrls.addAll(imagesToKeep);

        // Handle new images if provided
        if (newImages != null && newImages.length > 0) {
            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    String imageUrl = firebaseStorageService.uploadFile(image, "posts/images");
                    updatedImageUrls.add(imageUrl);
                }
            }
        }

        // Update post's image URLs
        post.setImageUrls(updatedImageUrls);

        // Handle new video if provided
        if (newVideo != null && !newVideo.isEmpty()) {
            // Delete old video
            if (post.getVideoUrl() != null && !post.getVideoUrl().isEmpty()) {
                try {
                    firebaseStorageService.deleteFile(post.getVideoUrl());
                } catch (Exception e) {
                    System.err.println("Failed to delete video: " + post.getVideoUrl() + ". Continuing...");
                }
            }

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
        if (post.getImageUrls() != null) {
            post.getImageUrls().forEach(url -> {
                try {
                    firebaseStorageService.deleteFile(url);
                } catch (Exception e) {
                    System.err.println("Failed to delete image: " + url + ". Continuing...");
                }
            });
        }
        if (post.getVideoUrl() != null && !post.getVideoUrl().isEmpty()) {
            firebaseStorageService.deleteFile(post.getVideoUrl());
        }

        // Delete post from Firestore
        firestoreService.deletePost(postId);
    }

    public List<Post> getUserPosts() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        return firestoreService.getUserPosts(userId);
    }

    public Post getPostById(String postId) throws ExecutionException, InterruptedException {
        return firestoreService.getPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    public List<Post> getAllPosts() throws ExecutionException, InterruptedException {
        return firestoreService.getAllPosts();
    }
}

