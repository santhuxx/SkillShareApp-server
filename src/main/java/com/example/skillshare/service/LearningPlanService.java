package com.example.skillshare.service;

import com.example.skillshare.model.LearningPlan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class LearningPlanService {

    private final FirestoreService firestoreService;

    public LearningPlanService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public LearningPlan createLearningPlan(String title, String description,
                                           String startDate, String endDate)
            throws ExecutionException, InterruptedException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        // Create learning plan
        LearningPlan learningPlan = new LearningPlan();
        learningPlan.setUserId(userId);
        learningPlan.setTitle(title);
        learningPlan.setDescription(description);
        learningPlan.setStartDate(startDate);
        learningPlan.setEndDate(endDate);

        return firestoreService.createLearningPlan(learningPlan);
    }

    public LearningPlan updateLearningPlan(String planId, String title, String description,
                                           String startDate, String endDate)
            throws ExecutionException, InterruptedException {

        LearningPlan learningPlan = firestoreService.getLearningPlanById(planId)
                .orElseThrow(() -> new RuntimeException("Learning plan not found"));

        // Update fields if provided
        if (title != null) learningPlan.setTitle(title);
        if (description != null) learningPlan.setDescription(description);
        if (startDate != null) learningPlan.setStartDate(startDate);
        if (endDate != null) learningPlan.setEndDate(endDate);

        return firestoreService.updateLearningPlan(planId, learningPlan);
    }

    public void deleteLearningPlan(String planId) throws ExecutionException, InterruptedException {
        firestoreService.getLearningPlanById(planId)
                .orElseThrow(() -> new RuntimeException("Learning plan not found"));

        // Delete learning plan from Firestore
        firestoreService.deleteLearningPlan(planId);
    }

    public List<LearningPlan> getUserLearningPlans() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getName();

        return firestoreService.getUserLearningPlans(userId);
    }

    public LearningPlan getLearningPlanById(String planId) throws ExecutionException, InterruptedException {
        return firestoreService.getLearningPlanById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Learning plan not found"));
    }

    public List<LearningPlan> getAllLearningPlans() throws ExecutionException, InterruptedException {
        return firestoreService.getAllLearningPlans();
    }
}