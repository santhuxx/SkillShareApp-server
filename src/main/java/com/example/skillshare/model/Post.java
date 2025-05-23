package com.example.skillshare.model;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Post {
    private String id;
    private String userId;
    private String username; // Added username field
    private String title;
    private String description;
    private List<String> imageUrls;
    private String videoUrl;
    private Date createdAt;
    private Date updatedAt;
    private int likeCount = 0;
    private int commentCount = 0;
}