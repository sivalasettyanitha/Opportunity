
package com.opportunity360.reminder.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.event.entity.Event;
import com.opportunity360.notification.service.NotificationDeliveryService;
import com.opportunity360.notification.service.NotificationService;
import com.opportunity360.reminder.entity.Reminder;
import com.opportunity360.reminder.entity.ReminderStatus;
import com.opportunity360.reminder.repository.ReminderRepository;

@Component
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;
    private final NotificationService notificationService;
    private final NotificationDeliveryService deliveryService;

    public ReminderScheduler(
            ReminderRepository reminderRepository,
            NotificationService notificationService,
            NotificationDeliveryService deliveryService) {

        this.reminderRepository = reminderRepository;
        this.notificationService = notificationService;
        this.deliveryService = deliveryService;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processReminders() {

        List<Reminder> reminders =
                reminderRepository.findAllByStatus(
                        ReminderStatus.SCHEDULED);

        for (Reminder reminder : reminders) {

            try {

                Event event = reminder.getEvent();

                ZoneId eventZone =
                        ZoneId.of(event.getTimezone());

                LocalDateTime currentTime =
                        LocalDateTime.now(eventZone);

                if (!reminder.getReminderAt().isAfter(currentTime)) {

                    notificationService.createNotification(reminder);

                    boolean emailSent =
                            deliveryService.sendReminderEmail(reminder);

                    if (emailSent) {

                        reminder.setStatus(ReminderStatus.SENT);
                        reminderRepository.save(reminder);

                    } else if (
                            deliveryService.hasReachedMaxAttempts(reminder)) {

                        reminder.setStatus(ReminderStatus.FAILED);
                        reminderRepository.save(reminder);

                        System.out.println(
                                "Reminder " + reminder.getId()
                                + " marked as FAILED after maximum email attempts.");
                    }
                }

            } catch (Exception exception) {

                System.err.println(
                        "Error processing reminder ID: "
                        + reminder.getId());

                exception.printStackTrace();
            }
        }
    }
}
