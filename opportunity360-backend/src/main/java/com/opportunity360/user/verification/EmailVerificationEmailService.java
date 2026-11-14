package com.opportunity360.user.verification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

@Service

public class EmailVerificationEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")

    private String senderEmail;

    public EmailVerificationEmailService(JavaMailSender mailSender) {

        this.mailSender = mailSender;

    }

    public void sendOtpEmail(String recipientEmail, String otp) {

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

    }

}