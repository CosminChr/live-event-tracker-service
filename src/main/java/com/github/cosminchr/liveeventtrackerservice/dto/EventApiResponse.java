package com.github.cosminchr.liveeventtrackerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the response from the external API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventApiResponse {
    private String eventId;
    private String currentScore;
}
