package com.example.skillshare.model;

import lombok.Data;
import java.util.Date;

@Data
public class LearningPlan {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private Date createdAt;
    private Date updatedAt;
}