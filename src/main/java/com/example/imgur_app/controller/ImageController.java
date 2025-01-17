package com.example.imgur_app.controller;

import com.example.imgur_app.service.ImgurClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImgurClient imgurClient;

    public ImageController(ImgurClient imgurClient) {
        this.imgurClient = imgurClient;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String response = imgurClient.uploadImage(image.getBytes());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<?> viewImage(@PathVariable String imageId) {
        try {
            String response = imgurClient.viewImage(imageId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch image: " + e.getMessage());
        }
    }

    @DeleteMapping("/{deleteHash}")
    public ResponseEntity<?> deleteImage(@PathVariable String deleteHash) {
        try {
            String response = imgurClient.deleteImage(deleteHash);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete image: " + e.getMessage());
        }
    }
}
