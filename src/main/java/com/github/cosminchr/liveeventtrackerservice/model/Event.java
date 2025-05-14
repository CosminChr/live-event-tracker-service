package com.github.cosminchr.liveeventtrackerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a sports event that can be tracked.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String eventId;
    private EventStatus status;
    private String currentScore;
    private LocalDateTime lastUpdated;
    private LocalDateTime lastPolled;
}
