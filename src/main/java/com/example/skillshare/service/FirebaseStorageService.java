package com.example.skillshare.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        String fileName = folderName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
        return blob.getMediaLink();
    }

    public void deleteFile(String fileUrl) {
        // Extract file path from URL and implement deletion logic
        // Example implementation:
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String filePath = extractFilePathFromUrl(fileUrl);
            bucket.get(filePath).delete();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file: " + fileUrl, e);
        }
    }

    private String extractFilePathFromUrl(String fileUrl) {
        // Implement logic to extract file path from Firebase Storage URL
        // This is a simplified version - adjust based on your actual URL format
        String baseUrl = "https://storage.googleapis.com/your-app-name.appspot.com/";
        return fileUrl.replace(baseUrl, "");
    }
}