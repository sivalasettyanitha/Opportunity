
package com.opportunity360.user.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.opportunity360.security.JwtService;
import com.opportunity360.user.dto.RegisterRequest;
import com.opportunity360.user.dto.LoginRequest;
import com.opportunity360.user.dto.VerifyEmailRequest;
import com.opportunity360.user.dto.ResendOtpRequest;
import com.opportunity360.user.dto.UpdateProfileRequest;
import com.opportunity360.user.dto.ChangePasswordRequest;
import com.opportunity360.user.entity.User;
import com.opportunity360.user.service.UserService;
import com.opportunity360.user.verification.EmailVerificationService;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    public UserController(
            UserService userService,
            JwtService jwtService,
            EmailVerificationService emailVerificationService) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.emailVerificationService = emailVerificationService;
    }

    // START USER REGISTRATION AND SEND OTP
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request) {

        try {
            emailVerificationService.startVerification(
                request.getName(),
                request.getEmail(),
                request.getPassword()
            );

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                Map.of(
                    "message",
                    "OTP sent successfully! Please check your email to verify your account."
                )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );
        }
    }

    // VERIFY EMAIL OTP AND CREATE ACCOUNT
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {

        try {
            boolean verified = emailVerificationService.verifyOtp(
                request.getEmail(),
                request.getOtp()
            );

            if (!verified) {
                return ResponseEntity.badRequest().body(
                    Map.of(
                        "message",
                        "Invalid or expired OTP. Please request a new OTP."
                    )
                );
            }

            return ResponseEntity.ok(
                Map.of(
                    "message",
                    "Email verified successfully! Your account has been created. You can now login."
                )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );
        }
    }

    // RESEND OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, String>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        try {
            emailVerificationService.resendOtp(request.getEmail());

            return ResponseEntity.ok(
                Map.of(
                    "message",
                    "A new OTP has been sent to your email."
                )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );
        }
    }

    // USER LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            User user = userService.loginUser(
                request.getEmail(),
                request.getPassword()
            );

            if (!user.isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                        "message",
                        "Please verify your email before logging in."
                    )
                );
            }

            String token = jwtService.generateToken(user.getEmail());

            return ResponseEntity.ok(
                Map.of(
                    "message", "Login successful!",
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole(),
                    "token", token
                )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message", e.getMessage())
            );
        }
    }

    // UPDATE USER PROFILE
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        User user = userService.updateProfile(
            authentication.getName(),
            request.getName()
        );

        return ResponseEntity.ok(
            Map.of(
                "message", "Profile updated successfully!",
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
            )
        );
    }

    // CHANGE PASSWORD
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        try {
            userService.changePassword(
                authentication.getName(),
                request.getCurrentPassword(),
                request.getNewPassword()
            );

            return ResponseEntity.ok(
                Map.of(
                    "message", "Password changed successfully!"
                )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );
        }
    }
}
