
package com.opportunity360.user.verification;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.user.entity.User;
import com.opportunity360.user.repository.UserRepository;

@Service
public class EmailVerificationService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final int MAX_RESENDS_PER_HOUR = 3;

    private final EmailVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationEmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public EmailVerificationService(
            EmailVerificationRepository verificationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationEmailService emailService) {

        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // START REGISTRATION AND SEND OTP
    @Transactional
    public String startVerification(
            String name,
            String email,
            String password) {

        email = email.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "This email is already registered. Please login.");
        }

        // Remove an expired pending registration.
        verificationRepository.findByEmail(email).ifPresent(existing -> {

            if (LocalDateTime.now().isAfter(existing.getExpiresAt())) {
                verificationRepository.delete(existing);
            } else {
                throw new IllegalArgumentException(
                        "An OTP has already been requested. Please use resend OTP.");
            }
        });

        String otp = generateOtp();
        LocalDateTime now = LocalDateTime.now();

        EmailVerification verification = new EmailVerification();

        verification.setName(name.trim());
        verification.setEmail(email);
        verification.setPasswordHash(passwordEncoder.encode(password));
        verification.setOtpHash(passwordEncoder.encode(otp));
        verification.setExpiresAt(now.plusMinutes(OTP_EXPIRY_MINUTES));
        verification.setLastSentAt(now);
        verification.setAttempts(0);
        verification.setResendCount(0);
        verification.setResendWindowStartedAt(now);

        // Save pending verification record first so the user can verify regardless of email transport
        verificationRepository.save(verification);

        boolean emailSent = emailService.sendOtpEmail(email, otp);

        return emailSent ? null : otp;
    }

    // VERIFY OTP AND CREATE USER ACCOUNT
    @Transactional
    public boolean verifyOtp(String email, String otp) {

        email = email.trim().toLowerCase();

        EmailVerification verification =
                verificationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No pending verification found. Please register again."));

        if (verification.getAttempts() >= MAX_ATTEMPTS) {
            verificationRepository.delete(verification);
            return false;
        }

        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            verificationRepository.delete(verification);
            return false;
        }

        if (!passwordEncoder.matches(otp, verification.getOtpHash())) {

            verification.setAttempts(verification.getAttempts() + 1);
            verificationRepository.save(verification);

            if (verification.getAttempts() >= MAX_ATTEMPTS) {
                verificationRepository.delete(verification);
            }

            return false;
        }

        if (userRepository.existsByEmail(email)) {
            verificationRepository.delete(verification);
            throw new IllegalArgumentException(
                    "This email is already registered. Please login.");
        }

        User user = new User();

        user.setName(verification.getName());
        user.setEmail(verification.getEmail());
        user.setPassword(verification.getPasswordHash());
        user.setRole("USER");
        user.setEmailVerified(true);

        userRepository.save(user);

        verificationRepository.delete(verification);

        return true;
    }

    // RESEND OTP WITH RATE LIMITING
    @Transactional
    public String resendOtp(String email) {

        email = email.trim().toLowerCase();

        EmailVerification verification =
                verificationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No pending registration found. Please register first."));

        LocalDateTime now = LocalDateTime.now();

        // Reject expired OTPs.
        if (now.isAfter(verification.getExpiresAt())) {
            verificationRepository.delete(verification);

            throw new IllegalArgumentException(
                    "Your OTP has expired. Please register again.");
        }

        // Check 60-second cooldown.
        if (verification.getLastSentAt()
                .plusSeconds(RESEND_COOLDOWN_SECONDS).isAfter(now)) {

            throw new IllegalArgumentException(
                    "Please wait 60 seconds before requesting another OTP.");
        }

        // Reset hourly resend window when one hour has passed.
        LocalDateTime windowStart =
                verification.getResendWindowStartedAt();

        if (windowStart == null ||
                !windowStart.plusHours(1).isAfter(now)) {

            verification.setResendWindowStartedAt(now);
            verification.setResendCount(0);
        }

        if (verification.getResendCount() >= MAX_RESENDS_PER_HOUR) {
            throw new IllegalArgumentException(
                    "You have reached the OTP resend limit. Please try again after one hour.");
        }

        String otp = generateOtp();

        verification.setOtpHash(passwordEncoder.encode(otp));
        verification.setExpiresAt(now.plusMinutes(OTP_EXPIRY_MINUTES));
        verification.setLastSentAt(now);
        verification.setAttempts(0);
        verification.setResendCount(
                verification.getResendCount() + 1);

        verificationRepository.save(verification);

        boolean emailSent = emailService.sendOtpEmail(email, otp);

        return emailSent ? null : otp;
    }

    // GENERATE SECURE SIX-DIGIT OTP
    private String generateOtp() {

        int number = secureRandom.nextInt(1_000_000);

        return String.format("%06d", number);
    }
}
