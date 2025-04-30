package com.example.skillshare.service;

import com.example.skillshare.model.Comment;
import com.example.skillshare.model.Post;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CommentService {

    private final FirestoreService firestoreService;

    public CommentService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public Comment createComment(String postId, String content) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        // Create comment
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreatedAt(new Date());
        comment.setUpdatedAt(new Date());

        // Update post comment count
        Post post = firestoreService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setCommentCount(post.getCommentCount() + 1);
        firestoreService.updatePost(postId, post);

        return firestoreService.createComment(comment);
    }

    public Comment updateComment(String commentId, String content) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        // Get comment
        Comment comment = firestoreService.getCommentById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if user is the owner of the comment
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this comment");
        }

        // Update comment
        comment.setContent(content);
        comment.setUpdatedAt(new Date());

        return firestoreService.updateComment(commentId, comment);
    }

    public void deleteComment(String commentId) throws ExecutionException, InterruptedException {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        // Get comment
        Comment comment = firestoreService.getCommentById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if user is the owner of the comment
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        // Update post comment count
        Post post = firestoreService.getPostById(comment.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setCommentCount(post.getCommentCount() - 1);
        firestoreService.updatePost(comment.getPostId(), post);

        // Delete comment
        firestoreService.deleteComment(commentId);
    }

    public List<Comment> getCommentsByPostId(String postId) throws ExecutionException, InterruptedException {
        System.out.println("Fetching comments for post: " + postId);
        try {
            List<Comment> comments = firestoreService.getCommentsByPostId(postId);
            System.out.println("Found " + comments.size() + " comments");
            return comments;
        } catch (Exception e) {
            System.err.println("Error in CommentService.getCommentsByPostId: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}