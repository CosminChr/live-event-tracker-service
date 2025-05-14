package com.github.cosminchr.liveeventtrackerservice.service;

import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;

/**
 * Service interface for publishing messages to Kafka.
 */
public interface MessagePublisherService {
    
    /**
     * Publishes an event update message to Kafka.
     *
     * @param message The message to publish
     */
    void publishEventUpdate(EventUpdateMessage message);
}
