package com.github.cosminchr.liveeventtrackerservice.scheduler;

import com.github.cosminchr.liveeventtrackerservice.dto.EventApiResponse;
import com.github.cosminchr.liveeventtrackerservice.dto.EventStatusUpdateRequest;
import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;
import com.github.cosminchr.liveeventtrackerservice.model.Event;
import com.github.cosminchr.liveeventtrackerservice.model.EventStatus;
import com.github.cosminchr.liveeventtrackerservice.service.EventService;
import com.github.cosminchr.liveeventtrackerservice.service.ExternalApiService;
import com.github.cosminchr.liveeventtrackerservice.service.MessagePublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scheduler for polling live events.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EventPollingScheduler {

    private final EventService eventService;
    private final ExternalApiService externalApiService;
    private final MessagePublisherService messagePublisherService;

    // Track events that are currently being polled
    private final Map<String, Boolean> activePollingEvents = new ConcurrentHashMap<>();

    /**
     * Scheduled task that runs every 10 seconds to poll for live event updates.
     */
    @Scheduled(fixedRate = 10000) // 10 seconds in milliseconds
    public void pollLiveEvents() {
        log.info("Starting scheduled polling of live events: activeEvents={}", activePollingEvents.size());

        // For each event in the activePollingEvents map, check if it's still live
        activePollingEvents.forEach((eventId, isPolling) -> {
            Event event = eventService.getEvent(eventId);

            // If the event is no longer live, remove it from the polling map
            if (event == null || event.getStatus() != EventStatus.LIVE) {
                log.info("Event is no longer live, removing from polling: eventId={}", eventId);
                activePollingEvents.remove(eventId);
                return;
            }

            // If the event is live, poll for updates
            try {
                pollEvent(event);
            } catch (Exception e) {
                log.error("Error polling event: eventId={}, error={}", eventId, e.getMessage());
            }
        });
    }

    /**
     * Adds an event to the polling schedule.
     *
     * @param eventId The event ID
     */
    public void scheduleEventPolling(String eventId) {
        log.info("Scheduling event for polling: eventId={}", eventId);
        activePollingEvents.put(eventId, true);
    }

    /**
     * Removes an event from the polling schedule.
     *
     * @param eventId The event ID
     */
    public void unscheduleEventPolling(String eventId) {
        log.info("Unscheduling event from polling: eventId={}", eventId);
        activePollingEvents.remove(eventId);
    }

    /**
     * Polls a single event for updates.
     *
     * @param event The event to poll
     */
    private void pollEvent(Event event) {
        String eventId = event.getEventId();

        log.info("Polling event: eventId={}", eventId);

        // Update the last polled timestamp
        event.setLastPolled(LocalDateTime.now());

        // Fetch the latest data from the external API
        EventApiResponse apiResponse = externalApiService.fetchEventData(eventId);

        if (apiResponse == null) {
            log.warn("Received null response from external API: eventId={}", eventId);
            return;
        }

        // Update the event with the latest data
        event.setCurrentScore(apiResponse.getCurrentScore());

        // Create and publish the event update message
        EventUpdateMessage message = EventUpdateMessage.builder()
                .eventId(apiResponse.getEventId())
                .currentScore(apiResponse.getCurrentScore())
                .timestamp(LocalDateTime.now())
                .build();

        messagePublisherService.publishEventUpdate(message);

        log.debug("Event polled successfully: eventId={}, currentScore={}",
                eventId, apiResponse.getCurrentScore());
    }
}
