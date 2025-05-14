package com.github.cosminchr.liveeventtrackerservice.service;

import com.github.cosminchr.liveeventtrackerservice.dto.EventApiResponse;
import com.github.cosminchr.liveeventtrackerservice.service.impl.ExternalApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalApiServiceImpl externalApiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(externalApiService, "baseUrl", "http://test-api.com");
        ReflectionTestUtils.setField(externalApiService, "eventEndpoint", "/events/{eventId}");
    }

    @Test
    void fetchEventData_WhenApiReturnsData_ShouldReturnEventApiResponse() {
        // Arrange
        String eventId = "event123";
        EventApiResponse expectedResponse = new EventApiResponse(eventId, "2:1");

        when(restTemplate.getForObject(
                eq("http://test-api.com/events/event123"),
                eq(EventApiResponse.class)))
                .thenReturn(expectedResponse);

        // Act
        EventApiResponse result = externalApiService.fetchEventData(eventId);

        // Assert
        assertNotNull(result);
        assertEquals(eventId, result.getEventId());
        assertEquals("2:1", result.getCurrentScore());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(EventApiResponse.class));
    }

    @Test
    void fetchEventData_WhenApiReturnsNull_ShouldReturnNull() {
        // Arrange
        String eventId = "event123";

        when(restTemplate.getForObject(anyString(), eq(EventApiResponse.class)))
                .thenReturn(null);

        // Act
        EventApiResponse result = externalApiService.fetchEventData(eventId);

        // Assert
        assertNull(result);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(EventApiResponse.class));
    }

    @Test
    void fetchEventData_WhenApiThrowsException_ShouldPropagateException() {
        // Arrange
        String eventId = "event123";
        RestClientException expectedException = new RestClientException("API error");

        when(restTemplate.getForObject(anyString(), eq(EventApiResponse.class)))
                .thenThrow(expectedException);

        // Act & Assert
        RestClientException exception = assertThrows(
                RestClientException.class,
                () -> externalApiService.fetchEventData(eventId)
        );

        assertEquals("API error", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(EventApiResponse.class));
    }
}
