package com.example.skillshare.service;

import com.example.skillshare.model.Like;
import com.example.skillshare.model.Post;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class LikeService {

    private final FirestoreService firestoreService;

    public LikeService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public Like addLike(String postId) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        // Check if user already liked the post
        Optional<Like> existingLike = firestoreService.getLikeByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            throw new RuntimeException("You have already liked this post");
        }

        // Create like
        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        like.setCreatedAt(new Date());

        // Update post like count
        Post post = firestoreService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikeCount(post.getLikeCount() + 1);
        firestoreService.updatePost(postId, post);

        return firestoreService.createLike(like);
    }

    public void removeLike(String postId) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        // Get like
        Like like = firestoreService.getLikeByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("You have not liked this post"));

        // Update post like count
        Post post = firestoreService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikeCount(post.getLikeCount() - 1);
        firestoreService.updatePost(postId, post);

        // Delete like
        firestoreService.deleteLike(like.getId());
    }

    public boolean hasUserLikedPost(String postId) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        return firestoreService.getLikeByPostIdAndUserId(postId, userId).isPresent();
    }

    public int getLikeCountByPostId(String postId) throws ExecutionException, InterruptedException {
        return firestoreService.getLikeCountByPostId(postId);
    }
}