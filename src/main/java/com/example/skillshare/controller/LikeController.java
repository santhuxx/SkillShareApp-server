package com.example.skillshare.controller;

import com.example.skillshare.model.Like;
import com.example.skillshare.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Like> addLike(
            @PathVariable String postId) throws ExecutionException, InterruptedException {

        Like like = likeService.addLike(postId);
        return ResponseEntity.ok(like);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeLike(
            @PathVariable String postId) throws ExecutionException, InterruptedException {

        likeService.removeLike(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable String postId) throws ExecutionException, InterruptedException {

        boolean hasLiked = likeService.hasUserLikedPost(postId);
        int likeCount = likeService.getLikeCountByPostId(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("hasLiked", hasLiked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }
}