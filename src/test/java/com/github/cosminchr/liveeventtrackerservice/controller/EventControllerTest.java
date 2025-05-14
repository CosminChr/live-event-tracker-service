package com.github.cosminchr.liveeventtrackerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cosminchr.liveeventtrackerservice.config.TestConfig;
import com.github.cosminchr.liveeventtrackerservice.dto.EventStatusUpdateRequest;
import com.github.cosminchr.liveeventtrackerservice.model.Event;
import com.github.cosminchr.liveeventtrackerservice.model.EventStatus;
import com.github.cosminchr.liveeventtrackerservice.scheduler.EventPollingScheduler;
import com.github.cosminchr.liveeventtrackerservice.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "event-tracker.external-api.base-url=http://localhost:8081/api",
        "event-tracker.kafka.event-updates-topic=test-event-updates"
})
@Import(TestConfig.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private EventPollingScheduler eventPollingScheduler;

    @Test
    void updateEventStatus_WhenEventIsLive_ShouldSchedulePolling() throws Exception {
        // Arrange
        EventStatusUpdateRequest request = new EventStatusUpdateRequest("event123", true);

        Event event = new Event();
        event.setEventId("event123");
        event.setStatus(EventStatus.LIVE);
        event.setLastUpdated(LocalDateTime.now());

        when(eventService.updateEventStatus(any())).thenReturn(event);

        // Act & Assert
        mockMvc.perform(post("/api/events/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("event123"))
                .andExpect(jsonPath("$.status").value("LIVE"));

        verify(eventPollingScheduler, times(1)).scheduleEventPolling("event123");
        verify(eventPollingScheduler, never()).unscheduleEventPolling(any());
    }

    @Test
    void updateEventStatus_WhenEventIsNotLive_ShouldUnschedulePolling() throws Exception {
        // Arrange
        EventStatusUpdateRequest request = new EventStatusUpdateRequest("event123", false);

        Event event = new Event();
        event.setEventId("event123");
        event.setStatus(EventStatus.NOT_LIVE);
        event.setLastUpdated(LocalDateTime.now());

        when(eventService.updateEventStatus(any())).thenReturn(event);

        // Act & Assert
        mockMvc.perform(post("/api/events/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("event123"))
                .andExpect(jsonPath("$.status").value("NOT_LIVE"));

        verify(eventPollingScheduler, never()).scheduleEventPolling(any());
        verify(eventPollingScheduler, times(1)).unscheduleEventPolling("event123");
    }

    @Test
    void updateEventStatus_WhenRequestIsInvalid_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EventStatusUpdateRequest request = new EventStatusUpdateRequest("", null);

        // Act & Assert
        mockMvc.perform(post("/api/events/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEventStatus(any());
        verify(eventPollingScheduler, never()).scheduleEventPolling(any());
        verify(eventPollingScheduler, never()).unscheduleEventPolling(any());
    }
}
