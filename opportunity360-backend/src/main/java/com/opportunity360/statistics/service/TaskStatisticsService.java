
package com.opportunity360.statistics.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.action.entity.ActionStatus;
import com.opportunity360.action.entity.EventAction;
import com.opportunity360.action.repository.EventActionRepository;
import com.opportunity360.statistics.dto.TaskStatisticsResponse;

@Service
public class TaskStatisticsService {

    private final EventActionRepository actionRepository;

    public TaskStatisticsService(
            EventActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @Transactional(readOnly = true)
    public TaskStatisticsResponse getTaskStatistics(String email) {

        // Fetch all actions belonging to the logged-in user.
        List<EventAction> actions =
                actionRepository
                        .findAllByEvent_User_EmailOrderByDueAtAsc(email);

        long totalActions = actions.size();

        // Count pending actions.
        long pendingActions = actions.stream()
                .filter(action ->
                        action.getStatus() == ActionStatus.PENDING)
                .count();

        // Count in-progress actions.
        long inProgressActions = actions.stream()
                .filter(action ->
                        action.getStatus() == ActionStatus.IN_PROGRESS)
                .count();

        // Count completed actions.
        long completedActions = actions.stream()
                .filter(action ->
                        action.getStatus() == ActionStatus.COMPLETED)
                .count();

        // Count cancelled actions.
        long cancelledActions = actions.stream()
                .filter(action ->
                        action.getStatus() == ActionStatus.CANCELLED)
                .count();

        // Count overdue unfinished actions.
        LocalDateTime now = LocalDateTime.now();

        long overdueActions = actions.stream()
                .filter(action ->
                        action.getDueAt() != null)
                .filter(action ->
                        action.getDueAt().isBefore(now))
                .filter(action ->
                        action.getStatus() == ActionStatus.PENDING
                        || action.getStatus() == ActionStatus.IN_PROGRESS)
                .count();

        return new TaskStatisticsResponse(
                totalActions,
                pendingActions,
                inProgressActions,
                completedActions,
                cancelledActions,
                overdueActions);
    }
}
