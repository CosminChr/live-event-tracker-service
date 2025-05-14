package com.github.cosminchr.liveeventtrackerservice.service;

import com.github.cosminchr.liveeventtrackerservice.dto.EventStatusUpdateRequest;
import com.github.cosminchr.liveeventtrackerservice.model.Event;
import com.github.cosminchr.liveeventtrackerservice.model.EventStatus;
import com.github.cosminchr.liveeventtrackerservice.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {
    
    private EventService eventService;
    
    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl();
    }
    
    @Test
    void updateEventStatus_WhenEventIsLive_ShouldUpdateStatusToLive() {
        // Arrange
        EventStatusUpdateRequest request = new EventStatusUpdateRequest("event123", true);
        
        // Act
        Event result = eventService.updateEventStatus(request);
        
        // Assert
        assertNotNull(result);
        assertEquals("event123", result.getEventId());
        assertEquals(EventStatus.LIVE, result.getStatus());
        assertNotNull(result.getLastUpdated());
    }
    
    @Test
    void updateEventStatus_WhenEventIsNotLive_ShouldUpdateStatusToNotLive() {
        // Arrange
        EventStatusUpdateRequest request = new EventStatusUpdateRequest("event123", false);
        
        // Act
        Event result = eventService.updateEventStatus(request);
        
        // Assert
        assertNotNull(result);
        assertEquals("event123", result.getEventId());
        assertEquals(EventStatus.NOT_LIVE, result.getStatus());
        assertNotNull(result.getLastUpdated());
    }
    
    @Test
    void getEvent_WhenEventExists_ShouldReturnEvent() {
        // Arrange
        EventStatusUpdateRequest request = new EventStatusUpdateRequest("event123", true);
        eventService.updateEventStatus(request);
        
        // Act
        Event result = eventService.getEvent("event123");
        
        // Assert
        assertNotNull(result);
        assertEquals("event123", result.getEventId());
        assertEquals(EventStatus.LIVE, result.getStatus());
    }
    
    @Test
    void getEvent_WhenEventDoesNotExist_ShouldReturnNull() {
        // Act
        Event result = eventService.getEvent("nonexistent");
        
        // Assert
        assertNull(result);
    }
}
