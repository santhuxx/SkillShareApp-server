package com.example.skillshare.controller;

import com.example.skillshare.model.LearningPlan;
import com.example.skillshare.service.LearningPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/learning-plans")
public class LearningPlanController {

    @Autowired
    private final LearningPlanService learningPlanService;

    public LearningPlanController(LearningPlanService learningPlanService) {
        this.learningPlanService = learningPlanService;
    }

    @PostMapping
    public ResponseEntity<LearningPlan> createLearningPlan(
            @RequestBody LearningPlan request,
            @AuthenticationPrincipal OAuth2User principal) throws ExecutionException, InterruptedException {

        LearningPlan learningPlan = learningPlanService.createLearningPlan(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate()
        );
        return ResponseEntity.ok(learningPlan);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<LearningPlan> updateLearningPlan(
            @PathVariable String planId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws ExecutionException, InterruptedException {

        LearningPlan updatedPlan = learningPlanService.updateLearningPlan(planId, title, description, startDate, endDate);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteLearningPlan(@PathVariable String planId) throws ExecutionException, InterruptedException {
        learningPlanService.deleteLearningPlan(planId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LearningPlan>> getUserLearningPlans(@AuthenticationPrincipal OAuth2User principal) throws ExecutionException, InterruptedException {
        List<LearningPlan> plans = learningPlanService.getUserLearningPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<LearningPlan> getLearningPlanById(@PathVariable String planId) throws ExecutionException, InterruptedException {
        LearningPlan plan = learningPlanService.getLearningPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/all")
    public ResponseEntity<List<LearningPlan>> getAllLearningPlans() throws ExecutionException, InterruptedException {
        List<LearningPlan> plans = learningPlanService.getAllLearningPlans();
        return ResponseEntity.ok(plans);
    }
}

