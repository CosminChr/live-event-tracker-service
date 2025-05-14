package com.github.cosminchr.liveeventtrackerservice.scheduler;

import com.github.cosminchr.liveeventtrackerservice.dto.EventApiResponse;
import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;
import com.github.cosminchr.liveeventtrackerservice.model.Event;
import com.github.cosminchr.liveeventtrackerservice.model.EventStatus;
import com.github.cosminchr.liveeventtrackerservice.service.EventService;
import com.github.cosminchr.liveeventtrackerservice.service.ExternalApiService;
import com.github.cosminchr.liveeventtrackerservice.service.MessagePublisherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPollingSchedulerTest {
    
    @Mock
    private EventService eventService;
    
    @Mock
    private ExternalApiService externalApiService;
    
    @Mock
    private MessagePublisherService messagePublisherService;
    
    @InjectMocks
    private EventPollingScheduler eventPollingScheduler;
    
    private Event liveEvent;
    private Event notLiveEvent;
    
    @BeforeEach
    void setUp() {
        liveEvent = new Event();
        liveEvent.setEventId("live123");
        liveEvent.setStatus(EventStatus.LIVE);
        liveEvent.setLastUpdated(LocalDateTime.now());
        
        notLiveEvent = new Event();
        notLiveEvent.setEventId("notlive456");
        notLiveEvent.setStatus(EventStatus.NOT_LIVE);
        notLiveEvent.setLastUpdated(LocalDateTime.now());
    }
    
    @Test
    void pollLiveEvents_WhenEventIsLive_ShouldPollAndPublish() {
        // Arrange
        eventPollingScheduler.scheduleEventPolling("live123");
        
        when(eventService.getEvent("live123")).thenReturn(liveEvent);
        when(externalApiService.fetchEventData("live123")).thenReturn(new EventApiResponse("live123", "1:0"));
        
        // Act
        eventPollingScheduler.pollLiveEvents();
        
        // Assert
        verify(externalApiService, times(1)).fetchEventData("live123");
        verify(messagePublisherService, times(1)).publishEventUpdate(any(EventUpdateMessage.class));
    }
    
    @Test
    void pollLiveEvents_WhenEventIsNotLive_ShouldRemoveFromPolling() {
        // Arrange
        eventPollingScheduler.scheduleEventPolling("notlive456");
        
        when(eventService.getEvent("notlive456")).thenReturn(notLiveEvent);
        
        // Act
        eventPollingScheduler.pollLiveEvents();
        
        // Assert
        verify(externalApiService, never()).fetchEventData("notlive456");
        verify(messagePublisherService, never()).publishEventUpdate(any());
        
        // Verify it's removed from polling by calling pollLiveEvents again
        reset(eventService);
        eventPollingScheduler.pollLiveEvents();
        verify(eventService, never()).getEvent("notlive456");
    }
    
    @Test
    void pollLiveEvents_WhenApiReturnsNull_ShouldNotPublish() {
        // Arrange
        eventPollingScheduler.scheduleEventPolling("live123");
        
        when(eventService.getEvent("live123")).thenReturn(liveEvent);
        when(externalApiService.fetchEventData("live123")).thenReturn(null);
        
        // Act
        eventPollingScheduler.pollLiveEvents();
        
        // Assert
        verify(externalApiService, times(1)).fetchEventData("live123");
        verify(messagePublisherService, never()).publishEventUpdate(any());
    }
    
    @Test
    void pollLiveEvents_WhenApiThrowsException_ShouldHandleGracefully() {
        // Arrange
        eventPollingScheduler.scheduleEventPolling("live123");
        
        when(eventService.getEvent("live123")).thenReturn(liveEvent);
        when(externalApiService.fetchEventData("live123")).thenThrow(new RuntimeException("API error"));
        
        // Act
        eventPollingScheduler.pollLiveEvents();
        
        // Assert
        verify(externalApiService, times(1)).fetchEventData("live123");
        verify(messagePublisherService, never()).publishEventUpdate(any());
        
        // Verify it's still in the polling list
        reset(eventService, externalApiService);
        when(eventService.getEvent("live123")).thenReturn(liveEvent);
        
        eventPollingScheduler.pollLiveEvents();
        verify(eventService, times(1)).getEvent("live123");
    }
    
    @Test
    void scheduleEventPolling_ShouldAddEventToPollingList() {
        // Arrange
        when(eventService.getEvent("live123")).thenReturn(liveEvent);
        when(externalApiService.fetchEventData("live123")).thenReturn(new EventApiResponse("live123", "1:0"));
        
        // Act
        eventPollingScheduler.scheduleEventPolling("live123");
        eventPollingScheduler.pollLiveEvents();
        
        // Assert
        verify(externalApiService, times(1)).fetchEventData("live123");
    }
    
    @Test
    void unscheduleEventPolling_ShouldRemoveEventFromPollingList() {
        // Arrange
        eventPollingScheduler.scheduleEventPolling("live123");
        
        // Act
        eventPollingScheduler.unscheduleEventPolling("live123");
        eventPollingScheduler.pollLiveEvents();
        
        // Assert
        verify(eventService, never()).getEvent("live123");
        verify(externalApiService, never()).fetchEventData("live123");
    }
}
