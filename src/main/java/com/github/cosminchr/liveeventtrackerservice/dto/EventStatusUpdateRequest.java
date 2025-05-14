package com.github.cosminchr.liveeventtrackerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving event status updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusUpdateRequest {
    
    @NotBlank(message = "Event ID is required")
    private String eventId;
    
    @NotNull(message = "Status is required")
    private Boolean live;
}
