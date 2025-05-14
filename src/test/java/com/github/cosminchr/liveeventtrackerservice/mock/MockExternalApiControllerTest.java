package com.github.cosminchr.liveeventtrackerservice.mock;

import com.github.cosminchr.liveeventtrackerservice.dto.EventApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MockExternalApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getEventData_ShouldReturnEventData() {
        // Arrange
        String eventId = "test-event-" + System.currentTimeMillis();
        String url = "http://localhost:" + port + "/api/mock/events/" + eventId;

        // Act
        ResponseEntity<EventApiResponse> response = restTemplate.getForEntity(url, EventApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(eventId, response.getBody().getEventId());
        assertNotNull(response.getBody().getCurrentScore());
        assertTrue(response.getBody().getCurrentScore().matches("\\d+:\\d+"), "Score should be in format 'n:n'");
    }

    @Test
    void getEventData_WhenCalledMultipleTimes_ShouldReturnConsistentData() {
        // Arrange
        String eventId = "test-event-" + System.currentTimeMillis();
        String url = "http://localhost:" + port + "/api/mock/events/" + eventId;

        // Act - First call
        ResponseEntity<EventApiResponse> response1 = restTemplate.getForEntity(url, EventApiResponse.class);
        String initialScore = response1.getBody().getCurrentScore();

        // Act - Multiple calls to see if score changes
        boolean scoreChanged = false;
        for (int i = 0; i < 50; i++) {
            ResponseEntity<EventApiResponse> responseN = restTemplate.getForEntity(url, EventApiResponse.class);
            assertEquals(eventId, responseN.getBody().getEventId());

            if (!initialScore.equals(responseN.getBody().getCurrentScore())) {
                scoreChanged = true;
                break;
            }
        }

        // Assert - We don't assert that the score must change, as it's random
        // But we do assert that the response is always valid
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertNotNull(response1.getBody());
        assertEquals(eventId, response1.getBody().getEventId());
        assertTrue(initialScore.matches("\\d+:\\d+"), "Score should be in format 'n:n'");
    }
}
