
package com.opportunity360.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.opportunity360.user.entity.User;
import com.opportunity360.user.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserRepository userRepository;

    public UserProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET LOGGED-IN USER PROFILE
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(
            Authentication authentication) {

        // Get authenticated user's email from JWT
        String email = authentication.getName();

        // Find user in database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                    )
                );

        // Return profile details without password
        Map<String, Object> profile = Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole(),
            "createdAt", user.getCreatedAt()
        );

        return ResponseEntity.ok(profile);
    }
}
