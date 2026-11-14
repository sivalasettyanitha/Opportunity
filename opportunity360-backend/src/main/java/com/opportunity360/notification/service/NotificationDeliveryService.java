
package com.opportunity360.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.notification.dto.NotificationDeliveryResponse;
import com.opportunity360.notification.entity.DeliveryChannel;
import com.opportunity360.notification.entity.DeliveryStatus;
import com.opportunity360.notification.entity.NotificationDelivery;
import com.opportunity360.notification.repository.NotificationDeliveryRepository;
import com.opportunity360.notification.repository.NotificationPreferenceRepository;
import com.opportunity360.reminder.entity.Reminder;
import com.opportunity360.reminder.entity.ReminderStatus;
import com.opportunity360.reminder.repository.ReminderRepository;

@Service
public class NotificationDeliveryService {

    private final NotificationDeliveryRepository deliveryRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final ReminderRepository reminderRepository;
    private final EmailNotificationService emailNotificationService;

    @Value("${app.notifications.email.max-attempts:3}")
    private int maxAttempts;

    public NotificationDeliveryService(
            NotificationDeliveryRepository deliveryRepository,
            NotificationPreferenceRepository preferenceRepository,
            ReminderRepository reminderRepository,
            EmailNotificationService emailNotificationService) {

        this.deliveryRepository = deliveryRepository;
        this.preferenceRepository = preferenceRepository;
        this.reminderRepository = reminderRepository;
        this.emailNotificationService = emailNotificationService;
    }

    // Send an email only when the user has enabled email notifications.
    @Transactional
    public boolean sendReminderEmail(Reminder reminder) {

        String recipient = reminder.getEvent().getUser().getEmail();

        boolean emailEnabled = preferenceRepository
                .findByUser_Email(recipient)
                .map(preference -> preference.isEmailEnabled())
                .orElse(true);

        // Treat disabled email as successfully skipped.
        if (!emailEnabled) {
            return true;
        }

        NotificationDelivery delivery = deliveryRepository
                .findByReminder_IdAndChannel(
                        reminder.getId(),
                        DeliveryChannel.EMAIL)
                .orElseGet(() -> {

                    NotificationDelivery newDelivery =
                            new NotificationDelivery();

                    newDelivery.setReminder(reminder);
                    newDelivery.setChannel(DeliveryChannel.EMAIL);
                    newDelivery.setRecipient(recipient);
                    newDelivery.setStatus(DeliveryStatus.PENDING);
                    newDelivery.setAttempts(0);

                    return newDelivery;
                });

        if (delivery.getStatus() == DeliveryStatus.SENT) {
            return true;
        }

        if (delivery.getAttempts() >= maxAttempts) {
            delivery.setStatus(DeliveryStatus.FAILED);
            deliveryRepository.save(delivery);
            return false;
        }

        delivery.setRecipient(recipient);
        delivery.setAttempts(delivery.getAttempts() + 1);
        delivery.setLastAttemptAt(LocalDateTime.now());
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setErrorMessage(null);

        deliveryRepository.saveAndFlush(delivery);

        try {

            String eventTitle = reminder.getEvent().getTitle();

            String message = "Your reminder is due for the event: "
                    + eventTitle;

            emailNotificationService.sendReminderEmail(
                    recipient,
                    eventTitle,
                    message
            );

            delivery.setStatus(DeliveryStatus.SENT);
            delivery.setSentAt(LocalDateTime.now());

            deliveryRepository.save(delivery);

            return true;

        } catch (Exception exception) {

            delivery.setStatus(DeliveryStatus.FAILED);

            String error = exception.getMessage();

            if (error == null || error.isBlank()) {
                error = "Email delivery failed";
            }

            delivery.setErrorMessage(
                    error.substring(0, Math.min(error.length(), 1000))
            );

            deliveryRepository.save(delivery);

            return false;
        }
    }

    // Retrieve email delivery history.
    @Transactional(readOnly = true)
    public List<NotificationDeliveryResponse> getDeliveryHistory(
            String email) {

        return deliveryRepository
                .findAllByRecipientOrderByCreatedAtDesc(email)
                .stream()
                .map(NotificationDeliveryResponse::fromDelivery)
                .toList();
    }

    // Manually retry a failed email.
    @Transactional
    public boolean retryFailedDelivery(Long deliveryId, String email) {

        NotificationDelivery delivery = deliveryRepository
                .findByIdAndRecipient(deliveryId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notification delivery not found"));

        if (delivery.getStatus() != DeliveryStatus.FAILED) {
            throw new IllegalArgumentException(
                    "Only failed email deliveries can be retried");
        }

        if (delivery.getChannel() != DeliveryChannel.EMAIL) {
            throw new IllegalArgumentException(
                    "Only email deliveries can be retried");
        }

        delivery.setAttempts(0);
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setErrorMessage(null);
        delivery.setSentAt(null);

        deliveryRepository.save(delivery);

        boolean sent = sendReminderEmail(delivery.getReminder());

        if (sent) {
            Reminder reminder = delivery.getReminder();
            reminder.setStatus(ReminderStatus.SENT);
            reminderRepository.save(reminder);
        }

        return sent;
    }

    // Check whether the maximum retry count has been reached.
    @Transactional(readOnly = true)
    public boolean hasReachedMaxAttempts(Reminder reminder) {

        return deliveryRepository
                .findByReminder_IdAndChannel(
                        reminder.getId(),
                        DeliveryChannel.EMAIL)
                .map(delivery ->
                        delivery.getAttempts() >= maxAttempts
                        && delivery.getStatus() == DeliveryStatus.FAILED)
                .orElse(false);
    }
}
