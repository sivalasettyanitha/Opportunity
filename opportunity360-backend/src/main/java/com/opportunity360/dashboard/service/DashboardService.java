
package com.opportunity360.dashboard.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.action.entity.ActionStatus;
import com.opportunity360.action.entity.EventAction;
import com.opportunity360.action.repository.EventActionRepository;
import com.opportunity360.dashboard.dto.DashboardEventItem;
import com.opportunity360.dashboard.dto.DashboardReminderItem;
import com.opportunity360.dashboard.dto.DashboardResponse;
import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.repository.EventRepository;
import com.opportunity360.notification.repository.NotificationRepository;
import com.opportunity360.reminder.entity.Reminder;
import com.opportunity360.reminder.entity.ReminderStatus;
import com.opportunity360.reminder.repository.ReminderRepository;

@Service
public class DashboardService {

    private final EventRepository eventRepository;
    private final ReminderRepository reminderRepository;
    private final EventActionRepository actionRepository;
    private final NotificationRepository notificationRepository;

    public DashboardService(
            EventRepository eventRepository,
            ReminderRepository reminderRepository,
            EventActionRepository actionRepository,
            NotificationRepository notificationRepository) {

        this.eventRepository = eventRepository;
        this.reminderRepository = reminderRepository;
        this.actionRepository = actionRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email) {

        LocalDateTime now = LocalDateTime.now();

        // Get all events belonging to the user.
        List<Event> events =
                eventRepository
                        .findAllByUser_EmailOrderByEventDateAscEventTimeAsc(
                                email);

        // Count upcoming events.
        long upcomingEvents = events.stream()
                .filter(event ->
                        event.getStatus() == EventStatus.UPCOMING)
                .count();

        // Get the next five upcoming events.
        List<DashboardEventItem> recentEvents = events.stream()
                .filter(event ->
                        event.getStatus() == EventStatus.UPCOMING)
                .filter(event ->
                        event.getEventDate().atStartOfDay()
                                .isAfter(now.minusSeconds(1))
                        || event.getEventDate().equals(now.toLocalDate()))
                .limit(5)
                .map(DashboardEventItem::fromEvent)
                .toList();

        // Get all reminders belonging to the user.
        List<Reminder> reminders =
                reminderRepository
                        .findAllByEvent_User_EmailOrderByReminderAtAsc(
                                email);

        // Count scheduled reminders.
        long scheduledReminders = reminders.stream()
                .filter(reminder ->
                        reminder.getStatus() == ReminderStatus.SCHEDULED)
                .count();

        // Get the next five scheduled reminders.
        List<DashboardReminderItem> upcomingReminders =
                reminders.stream()
                        .filter(reminder ->
                                reminder.getStatus()
                                        == ReminderStatus.SCHEDULED)
                        .filter(reminder ->
                                !reminder.getReminderAt().isBefore(now))
                        .sorted(Comparator.comparing(
                                Reminder::getReminderAt))
                        .limit(5)
                        .map(DashboardReminderItem::fromReminder)
                        .toList();

        // Get all actions belonging to the user.
        List<EventAction> actions =
                actionRepository
                        .findAllByEvent_User_EmailOrderByDueAtAsc(email);

        // Count pending and in-progress actions.
        long pendingActions = actions.stream()
                .filter(action ->
                        action.getStatus() == ActionStatus.PENDING
                        || action.getStatus() == ActionStatus.IN_PROGRESS)
                .count();

        // Count unread notifications.
        long unreadNotifications =
                notificationRepository
                        .countByUser_EmailAndReadFalse(email);

        return new DashboardResponse(
                upcomingEvents,
                scheduledReminders,
                pendingActions,
                unreadNotifications,
                recentEvents,
                upcomingReminders);
    }
}
