// Message.java
package com.example.skillshare.model;

import lombok.Data;
import java.util.Date;

@Data
public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private Date timestamp;
    private boolean read;
    private boolean deleted;  // New field
    private Date updatedAt;   // New field for tracking updates
}

