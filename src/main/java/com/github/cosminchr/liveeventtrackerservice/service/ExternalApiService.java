package com.github.cosminchr.liveeventtrackerservice.service;

import com.github.cosminchr.liveeventtrackerservice.dto.EventApiResponse;

/**
 * Service interface for interacting with the external API.
 */
public interface ExternalApiService {
    
    /**
     * Fetches event data from the external API.
     *
     * @param eventId The event ID
     * @return The API response
     */
    EventApiResponse fetchEventData(String eventId);
}
