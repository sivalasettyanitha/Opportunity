
package com.opportunity360.calendar.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity360.calendar.dto.CalendarEventResponse;
import com.opportunity360.calendar.service.CalendarService;
import com.opportunity360.event.entity.EventCategory;
import com.opportunity360.event.entity.EventStatus;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public ResponseEntity<?> getCalendarEvents(
            Authentication authentication,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            EventCategory category,

            @RequestParam(required = false)
            EventStatus status,

            @RequestParam(required = false)
            String search,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        try {
            Page<CalendarEventResponse> events =
                    calendarService.getCalendarEvents(
                            authentication.getName(),
                            startDate,
                            endDate,
                            category,
                            status,
                            search,
                            page,
                            size);

            return ResponseEntity.ok(events);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }
}
