package com.github.cosminchr.liveeventtrackerservice.service.impl;

import com.github.cosminchr.liveeventtrackerservice.dto.EventStatusUpdateRequest;
import com.github.cosminchr.liveeventtrackerservice.model.Event;
import com.github.cosminchr.liveeventtrackerservice.model.EventStatus;
import com.github.cosminchr.liveeventtrackerservice.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the EventService interface.
 * Uses an in-memory store for events.
 */
@Service
@Slf4j
public class EventServiceImpl implements EventService {
    
    // In-memory store for events
    private final Map<String, Event> events = new ConcurrentHashMap<>();
    
    @Override
    public Event updateEventStatus(EventStatusUpdateRequest request) {
        String eventId = request.getEventId();
        EventStatus status = request.getLive() ? EventStatus.LIVE : EventStatus.NOT_LIVE;
        
        Event event = events.getOrDefault(eventId, Event.builder()
                .eventId(eventId)
                .build());
        
        event.setStatus(status);
        event.setLastUpdated(LocalDateTime.now());
        
        events.put(eventId, event);
        
        log.info("Updated event status: eventId={}, status={}", eventId, status);
        
        return event;
    }
    
    @Override
    public Event getEvent(String eventId) {
        return events.get(eventId);
    }
}
