
package com.opportunity360.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Send reminder email
    public void sendReminderEmail(
            String recipientEmail,
            String eventTitle,
            String message) {

        SimpleMailMessage email = new SimpleMailMessage();

        email.setFrom(senderEmail);
        email.setTo(recipientEmail);

        email.setSubject(
                "Opportunity360 - Reminder: " + eventTitle
        );

        email.setText(
                "Hello!\n\n"
                + message
                + "\n\nEvent: " + eventTitle
                + "\n\nDon't forget what matters!\n"
                + "Team Opportunity360"
        );

        mailSender.send(email);
    }
}
