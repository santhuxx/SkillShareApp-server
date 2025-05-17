package com.example.skillshare.model;

import lombok.Data;
import java.util.Date;

@Data
public class Notification {
    private String id;
    private String userId;
    private String type;
    private String postId;
    private String actorId;
    private String actorUsername;
    private String content;
    private Date createdAt;
    private boolean isRead;
}

