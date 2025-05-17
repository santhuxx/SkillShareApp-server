package com.example.skillshare.model;

import lombok.Data;
import java.util.Date;

@Data
public class Comment {
    private String id;
    private String postId;
    private String userId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}

