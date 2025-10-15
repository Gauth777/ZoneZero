package com.example.zonezero.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zonezero.model.User;
import com.example.zonezero.repository.UserRepository;

/**
 * REST controller for user registration. Consumes JSON payload and saves
 * it to the database using the repository.
 */
@RestController
@RequestMapping("/api/register")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * POST /api/register
     *
     * @param user incoming user record parsed from JSON
     * @return 201 Created on success, 400 if validation fails
     */
    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Basic validation: ensure non‑blank fields
        if (user.getName() == null || user.getName().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getPhone() == null || user.getPhone().trim().isEmpty() ||
            user.getRegion() == null || user.getRegion().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(java.util.Map.of("error", "Missing required fields"));
        }
        // Persist and return success
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(java.util.Map.of("ok", true));
    }
}