package com.example.imgur_app.controller;

import com.example.imgur_app.dto.UserRegistrationDTO;
import com.example.imgur_app.entity.User;
import com.example.imgur_app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        try {
            User user = userService.registerUser(userRegistrationDTO);
            return ResponseEntity.ok("User registered successfully with ID: " + user.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

