
package com.opportunity360.statistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity360.statistics.dto.TaskStatisticsResponse;
import com.opportunity360.statistics.service.TaskStatisticsService;

@RestController
@RequestMapping("/api/statistics")
public class TaskStatisticsController {

    private final TaskStatisticsService taskStatisticsService;

    public TaskStatisticsController(
            TaskStatisticsService taskStatisticsService) {
        this.taskStatisticsService = taskStatisticsService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<TaskStatisticsResponse> getTaskStatistics(
            Authentication authentication) {

        TaskStatisticsResponse response =
                taskStatisticsService.getTaskStatistics(
                        authentication.getName());

        return ResponseEntity.ok(response);
    }
}
