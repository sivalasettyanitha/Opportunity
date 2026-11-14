
package com.opportunity360.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.user.entity.User;
import com.opportunity360.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // USER REGISTRATION
    // Registration is now handled through EmailVerificationService.
    // This method is retained for compatibility with existing code.
    public User registerUser(String name, String email, String password) {

        email = email.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered!");
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setEmailVerified(false);

        return userRepository.save(user);
    }

    // USER LOGIN
    public User loginUser(String email, String password) {

        email = email.trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException(
                    "Please verify your email before logging in.");
        }

        return user;
    }

    // UPDATE USER PROFILE
    @Transactional
    public User updateProfile(String email, String newName) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"));

        user.setName(newName.trim());

        return userRepository.save(user);
    }

    // CHANGE PASSWORD
    @Transactional
    public void changePassword(
            String email,
            String currentPassword,
            String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"));

        if (!passwordEncoder.matches(
                currentPassword, user.getPassword())) {

            throw new IllegalArgumentException(
                    "Current password is incorrect");
        }

        if (passwordEncoder.matches(
                newPassword, user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}
