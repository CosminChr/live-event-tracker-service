package com.github.cosminchr.liveeventtrackerservice.service;

import com.github.cosminchr.liveeventtrackerservice.dto.EventStatusUpdateRequest;
import com.github.cosminchr.liveeventtrackerservice.model.Event;

/**
 * Service interface for managing events.
 */
public interface EventService {
    
    /**
     * Updates the status of an event.
     *
     * @param request The status update request
     * @return The updated event
     */
    Event updateEventStatus(EventStatusUpdateRequest request);
    
    /**
     * Gets an event by its ID.
     *
     * @param eventId The event ID
     * @return The event, or null if not found
     */
    Event getEvent(String eventId);
}
