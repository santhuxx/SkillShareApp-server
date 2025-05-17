package com.example.skillshare.config;

import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FirestoreInitializer implements CommandLineRunner {

    private final Firestore firestore;

    @Autowired
    public FirestoreInitializer(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void run(String... args) {
        // Create collections if they don't exist
        // Note: In Firestore, collections are created implicitly when documents are added
        // This is just to ensure the path exists for queries
        try {
            firestore.collection("comments").document("init").set(new HashMap<>());
            firestore.collection("likes").document("init").set(new HashMap<>());
            System.out.println("Initialized Firestore collections");
        } catch (Exception e) {
            System.err.println("Error initializing Firestore collections: " + e.getMessage());
        }
    }
}

