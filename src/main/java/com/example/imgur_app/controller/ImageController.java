package com.example.imgur_app.controller;

import com.example.imgur_app.service.ImgurClient;
import com.example.imgur_app.entity.User;
import com.example.imgur_app.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImgurClient imgurClient;
    private final UserRepository userRepository;

    public ImageController(ImgurClient imgurClient, UserRepository userRepository) {
        this.imgurClient = imgurClient;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image, Authentication authentication) {
        try {
            // Authenticate the user
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            // Upload the image to Imgur
            String response = imgurClient.uploadImage(image.getBytes());

            // Optionally, associate the image with the user (implement this if needed)
            // Save image metadata to the database if required.

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<?> viewImage(@PathVariable String imageId, Authentication authentication) {
        try {
            // Authenticate the user
            String username = authentication.getName();
            userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            // Fetch the image details from Imgur
            String response = imgurClient.viewImage(imageId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch image: " + e.getMessage());
        }
    }

    @DeleteMapping("/{deleteHash}")
    public ResponseEntity<?> deleteImage(@PathVariable String deleteHash, Authentication authentication) {
        try {
            // Authenticate the user
            String username = authentication.getName();
            userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            // Delete the image from Imgur
            String response = imgurClient.deleteImage(deleteHash);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete image: " + e.getMessage());
        }
    }
}
