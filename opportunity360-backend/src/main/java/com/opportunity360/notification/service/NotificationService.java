
package com.opportunity360.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.notification.entity.InAppNotification;
import com.opportunity360.notification.repository.NotificationRepository;
import com.opportunity360.notification.repository.NotificationPreferenceRepository;
import com.opportunity360.reminder.entity.Reminder;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationPreferenceRepository preferenceRepository) {

        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    // Create an in-app notification only if enabled.
    @Transactional
    public void createNotification(Reminder reminder) {

        String email = reminder.getEvent().getUser().getEmail();

        // Check the user's notification preference.
        boolean inAppEnabled = preferenceRepository
                .findByUser_Email(email)
                .map(preference -> preference.isInAppEnabled())
                .orElse(true);

        if (!inAppEnabled) {
            return;
        }

        // Avoid duplicate notifications for the same reminder.
        if (notificationRepository.existsByReminder_Id(reminder.getId())) {
            return;
        }

        InAppNotification notification = new InAppNotification();

        notification.setUser(reminder.getEvent().getUser());
        notification.setReminder(reminder);

        notification.setTitle(
                "Reminder: " + reminder.getEvent().getTitle());

        notification.setMessage(
                "Your event '" + reminder.getEvent().getTitle()
                + "' is approaching. Don't forget it!");

        notification.setRead(false);

        notificationRepository.save(notification);
    }

    // Get all notifications belonging to the logged-in user.
    @Transactional(readOnly = true)
    public List<InAppNotification> getAllNotifications(String email) {

        return notificationRepository
                .findAllByUser_EmailOrderByCreatedAtDesc(email);
    }

    // Get the unread notification count.
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {

        return notificationRepository
                .countByUser_EmailAndReadFalse(email);
    }

    // Mark a notification as read.
    @Transactional
    public InAppNotification markAsRead(
            Long notificationId, String email) {

        InAppNotification notification = notificationRepository
                .findByIdAndUser_Email(notificationId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notification not found"));

        notification.setRead(true);

        return notificationRepository.save(notification);
    }
}
