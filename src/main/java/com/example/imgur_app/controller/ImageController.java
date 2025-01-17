package com.example.imgur_app.controller;

import com.example.imgur_app.entity.Image;
import com.example.imgur_app.repository.ImageRepository;
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
    private final ImageRepository imageRepository;

    public ImageController(ImgurClient imgurClient, UserRepository userRepository, ImageRepository imageRepository) {
        this.imgurClient = imgurClient;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image, Authentication authentication) {
        try {
            // Authenticate the user
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            // Upload image to Imgur
            ImgurClient.ImageMetadata metadata = imgurClient.uploadImage(image.getBytes());

            // Save image metadata in the database
            Image savedImage = new Image();
            savedImage.setDeleteHash(metadata.getDeleteHash());
            savedImage.setLink(metadata.getLink());
            savedImage.setUser(user); // Associate the image with the authenticated user
            imageRepository.save(savedImage);

            return ResponseEntity.ok("Image uploaded and associated with user successfully!\n Image ID: " + savedImage.getId()
                    + "\n Delete Hash: " + savedImage.getDeleteHash()
                    + "\n Link: " + savedImage.getLink());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewImage(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            Image image = imageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + id));

            // Ensure the image belongs to the authenticated user
            if (!image.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Access denied");
            }

            return ResponseEntity.ok(image.getLink());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{deleteHash}")
    public ResponseEntity<?> deleteImage(@PathVariable String deleteHash, Authentication authentication) {
        try {
            // Authenticate the user
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            // Find the image by deleteHash
            Image image = (Image) imageRepository.findByDeleteHash(deleteHash)
                    .orElseThrow(() -> new IllegalArgumentException("Image not found with deleteHash: " + deleteHash));

            // Ensure the image belongs to the authenticated user
            if (!image.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Access denied: You cannot delete this image.");
            }

            // Delete the image from Imgur
            imgurClient.deleteImage(deleteHash);

            // Remove the image record from the database
            imageRepository.delete(image);

            return ResponseEntity.ok("Image deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete image: " + e.getMessage());
        }
    }

}
