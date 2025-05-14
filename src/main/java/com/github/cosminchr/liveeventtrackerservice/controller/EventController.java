package com.github.cosminchr.liveeventtrackerservice.controller;

import com.github.cosminchr.liveeventtrackerservice.dto.EventStatusUpdateRequest;
import com.github.cosminchr.liveeventtrackerservice.model.Event;
import com.github.cosminchr.liveeventtrackerservice.model.EventStatus;
import com.github.cosminchr.liveeventtrackerservice.scheduler.EventPollingScheduler;
import com.github.cosminchr.liveeventtrackerservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for event-related endpoints.
 */
@RestController
@RequestMapping("/api/events")
@Slf4j
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;
    private final EventPollingScheduler eventPollingScheduler;
    
    /**
     * Endpoint for updating the status of an event.
     *
     * @param request The status update request
     * @return The updated event
     */
    @PostMapping("/status")
    public ResponseEntity<Event> updateEventStatus(@Valid @RequestBody EventStatusUpdateRequest request) {
        log.info("Received event status update request: {}", request);
        
        Event updatedEvent = eventService.updateEventStatus(request);
        
        // If the event is now live, schedule it for polling
        if (updatedEvent.getStatus() == EventStatus.LIVE) {
            eventPollingScheduler.scheduleEventPolling(updatedEvent.getEventId());
        } else {
            // If the event is no longer live, unschedule it from polling
            eventPollingScheduler.unscheduleEventPolling(updatedEvent.getEventId());
        }
        
        return ResponseEntity.ok(updatedEvent);
    }
}
