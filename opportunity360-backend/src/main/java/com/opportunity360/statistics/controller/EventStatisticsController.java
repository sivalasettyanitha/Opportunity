
package com.opportunity360.statistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity360.statistics.dto.EventStatisticsResponse;
import com.opportunity360.statistics.service.EventStatisticsService;

@RestController
@RequestMapping("/api/statistics")
public class EventStatisticsController {

    private final EventStatisticsService statisticsService;

    public EventStatisticsController(
            EventStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/events")
    public ResponseEntity<EventStatisticsResponse> getEventStatistics(
            Authentication authentication) {

        EventStatisticsResponse response =
                statisticsService.getStatistics(
                        authentication.getName());

        return ResponseEntity.ok(response);
    }
}
