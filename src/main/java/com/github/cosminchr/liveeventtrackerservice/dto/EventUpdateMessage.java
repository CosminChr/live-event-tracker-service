package com.github.cosminchr.liveeventtrackerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Message format for publishing event updates to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateMessage {
    private String eventId;
    private String currentScore;
    private LocalDateTime timestamp;
}
