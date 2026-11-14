package com.opportunity360.user.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationEmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailVerificationEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendOtpEmail(String recipientEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(recipientEmail);
            message.setSubject("Opportunity360 - Email Verification");
            message.setText(
                "Hello,\n\n" +
                "Thank you for registering with Opportunity360!\n\n" +
                "Your email verification OTP is: " + otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n" +
                "Do not share this OTP with anyone.\n\n" +
                "If you did not request this verification, please ignore this email.\n\n" +
                "Regards,\n" +
                "Opportunity360 Team"
            );

            mailSender.send(message);
            log.info("Verification OTP email successfully sent to {}", recipientEmail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}. Error: {}", recipientEmail, e.getMessage());
            System.out.println("==================================================");
            System.out.println("[OPPORTUNITY360 OTP] Verification OTP for " + recipientEmail + " is: " + otp);
            System.out.println("==================================================");
            return false;
        }
    }
}