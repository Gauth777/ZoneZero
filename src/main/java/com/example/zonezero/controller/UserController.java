package com.example.zonezero.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.zonezero.model.User;
import com.example.zonezero.repository.UserRepository;
import java.util.Optional;

@RestController
@RequestMapping("/api/register")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> registerOrLogin(@RequestBody User user) {
        if (user.getName() == null || user.getName().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getPhone() == null || user.getPhone().trim().isEmpty() ||
            user.getRegion() == null || user.getRegion().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(java.util.Map.of("error", "Missing required fields"));
        }

        // ✅ Check if user already exists by email
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            return ResponseEntity.ok(java.util.Map.of(
                "message", "Welcome back!",
                "user", existing.get()
            ));
        }

        // 🚀 Otherwise create new
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(java.util.Map.of("message", "New user registered", "user", user));
    }
}
