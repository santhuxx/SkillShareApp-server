package com.example.skillshare.controller;

import com.example.skillshare.model.Post;
import com.example.skillshare.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam MultipartFile[] images,
            @RequestParam MultipartFile video,
            @AuthenticationPrincipal OAuth2User principal) throws IOException, ExecutionException, InterruptedException {

        if (images.length > 3) {
            throw new IllegalArgumentException("Maximum 3 images allowed");
        }

        Post post = postService.createPost(title, description, images, video);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable String postId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestParam(required = false) MultipartFile video) throws IOException, ExecutionException, InterruptedException {

        if (images != null && images.length > 3) {
            throw new IllegalArgumentException("Maximum 3 images allowed");
        }

        Post updatedPost = postService.updatePost(postId, title, description, images, video);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) throws ExecutionException, InterruptedException, IOException {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Post>> getUserPosts(@AuthenticationPrincipal OAuth2User principal) throws ExecutionException, InterruptedException {
        List<Post> posts = postService.getUserPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable String postId) throws ExecutionException, InterruptedException {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getAllPosts() throws ExecutionException, InterruptedException {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
}