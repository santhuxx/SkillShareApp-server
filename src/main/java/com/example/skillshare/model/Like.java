package com.example.skillshare.model;

import lombok.Data;
import java.util.Date;

@Data
public class Like {
    private String id;
    private String postId;
    private String userId;
    private Date createdAt;
}