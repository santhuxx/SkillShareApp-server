package com.example.skillshare.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        String fileName = folderName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        // Make file public
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        // Return public download URL
        return "https://firebasestorage.googleapis.com/v0/b/"
                + bucket.getName()
                + "/o/"
                + java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                + "?alt=media";
    }

    public void deleteFile(String fileUrl) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String filePath = extractFilePathFromUrl(fileUrl);
            Blob blob = bucket.get(filePath);
            if (blob != null) {
                blob.delete();
            } else {
                throw new RuntimeException("File not found: " + filePath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file: " + fileUrl, e);
        }
    }


    private String extractFilePathFromUrl(String fileUrl) {
        // Example of fileUrl:
        // https://firebasestorage.googleapis.com/v0/b/[bucket-name]/o/[folder%2FfileName]?alt=media
        try {
            String path = fileUrl.split("/o/")[1].split("\\?alt=")[0];
            return URLDecoder.decode(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Invalid file URL format: " + fileUrl);
        }
    }
}