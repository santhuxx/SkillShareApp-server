// ChatSession.java
package com.example.skillshare.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ChatSession {
    private String id;
    private String user1Id;
    private String user2Id;
    private List<String> messageIds;
    private Date lastUpdated;
}