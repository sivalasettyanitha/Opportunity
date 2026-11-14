
package com.opportunity360.statistics.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventCategory;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.repository.EventRepository;
import com.opportunity360.statistics.dto.EventStatisticsResponse;

@Service
public class EventStatisticsService {

    private final EventRepository eventRepository;

    public EventStatisticsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public EventStatisticsResponse getStatistics(String email) {

        // Fetch all events belonging to the logged-in user.
        List<Event> events =
                eventRepository
                        .findAllByUser_EmailOrderByEventDateAscEventTimeAsc(
                                email);

        // Calculate total events.
        long totalEvents = events.size();

        // Calculate upcoming events.
        long upcomingEvents = events.stream()
                .filter(event ->
                        event.getStatus() == EventStatus.UPCOMING)
                .count();

        // Calculate completed events.
        long completedEvents = events.stream()
                .filter(event ->
                        event.getStatus() == EventStatus.COMPLETED)
                .count();

        // Calculate cancelled events.
        long cancelledEvents = events.stream()
                .filter(event ->
                        event.getStatus() == EventStatus.CANCELLED)
                .count();

        // Calculate category-wise event counts.
        Map<String, Long> categoryCounts = new HashMap<>();

        for (EventCategory category : EventCategory.values()) {

            long count = events.stream()
                    .filter(event ->
                            event.getCategory() == category)
                    .count();

            categoryCounts.put(category.name(), count);
        }

        // Return event statistics.
        return new EventStatisticsResponse(
                totalEvents,
                upcomingEvents,
                completedEvents,
                cancelledEvents,
                categoryCounts);
    }
}
