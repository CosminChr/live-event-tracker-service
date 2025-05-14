package com.github.cosminchr.liveeventtrackerservice.mock;

import com.github.cosminchr.liveeventtrackerservice.dto.EventApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock controller for simulating the external API.
 * This is for testing purposes only.
 */
@RestController
@RequestMapping("/api/mock")
@Slf4j
public class MockExternalApiController {
    
    private final Map<String, String> eventScores = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    /**
     * Endpoint for getting event data.
     *
     * @param eventId The event ID
     * @return The event data
     */
    @GetMapping("/events/{eventId}")
    public EventApiResponse getEventData(@PathVariable String eventId) {
        log.debug("Mock API received request for event: {}", eventId);
        
        // Generate a random score if one doesn't exist for this event
        String score = eventScores.computeIfAbsent(eventId, id -> "0:0");
        
        // Randomly update the score (10% chance)
        if (random.nextInt(10) == 0) {
            int homeScore = Integer.parseInt(score.split(":")[0]);
            int awayScore = Integer.parseInt(score.split(":")[1]);
            
            // 50% chance to increment home score, 50% chance to increment away score
            if (random.nextBoolean()) {
                homeScore++;
            } else {
                awayScore++;
            }
            
            score = homeScore + ":" + awayScore;
            eventScores.put(eventId, score);
            
            log.debug("Updated score for event {}: {}", eventId, score);
        }
        
        return new EventApiResponse(eventId, score);
    }
}
