
package com.opportunity360.reminder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opportunity360.reminder.entity.Reminder;
import com.opportunity360.reminder.entity.ReminderStatus;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findAllByEvent_User_EmailOrderByReminderAtAsc(
            String email);

    Optional<Reminder> findByIdAndEvent_User_Email(
            Long id, String email);

    List<Reminder> findAllByStatus(ReminderStatus status);

    List<Reminder> findAllByEvent_IdAndStatus(
            Long eventId, ReminderStatus status);

    List<Reminder>
            findAllByOccurrence_IdAndEvent_User_EmailOrderByReminderAtAsc(
                    Long occurrenceId, String email);

    // Find scheduled reminders for one occurrence.
    List<Reminder> findAllByOccurrence_IdAndStatus(
            Long occurrenceId, ReminderStatus status);
}
