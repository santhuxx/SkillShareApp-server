package com.example.skillshare.model;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Post {
    private String id;
    private String userId;
    private String title;
    private String description;
    private List<String> imageUrls;
    private String videoUrl;
    private Date createdAt;
    private Date updatedAt;
}