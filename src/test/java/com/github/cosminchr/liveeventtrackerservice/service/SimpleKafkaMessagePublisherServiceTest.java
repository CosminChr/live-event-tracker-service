package com.github.cosminchr.liveeventtrackerservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;
import com.github.cosminchr.liveeventtrackerservice.service.impl.SimpleKafkaMessagePublisherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleKafkaMessagePublisherServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private SimpleKafkaMessagePublisherServiceImpl publisher;

    @BeforeEach
    void setUp() {
        publisher = Mockito.spy(new SimpleKafkaMessagePublisherServiceImpl(kafkaTemplate));

        // Replace the ObjectMapper with our mock
        ReflectionTestUtils.setField(publisher, "objectMapper", objectMapper);

        // Set the topic name
        ReflectionTestUtils.setField(publisher, "eventUpdatesTopic", "test-topic");
    }

    @Test
    void publishEventUpdate_WhenSuccessful_ShouldSendToKafka() throws JsonProcessingException {
        // Arrange
        EventUpdateMessage message = EventUpdateMessage.builder()
                .eventId("event123")
                .currentScore("2:1")
                .timestamp(LocalDateTime.now())
                .build();

        String messageJson = "{\"eventId\":\"event123\",\"currentScore\":\"2:1\",\"timestamp\":null}";
        when(objectMapper.writeValueAsString(message)).thenReturn(messageJson);

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        // Act
        publisher.publishEventUpdate(message);

        // Assert
        verify(objectMapper, times(1)).writeValueAsString(message);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate, times(1)).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                valueCaptor.capture()
        );

        assertEquals("test-topic", topicCaptor.getValue());
        assertEquals("event123", keyCaptor.getValue());
        assertEquals(messageJson, valueCaptor.getValue());
    }

    @Test
    void publishEventUpdate_WhenJsonProcessingFails_ShouldHandleException() throws JsonProcessingException {
        // Arrange
        EventUpdateMessage message = EventUpdateMessage.builder()
                .eventId("event123")
                .currentScore("2:1")
                .timestamp(LocalDateTime.now())
                .build();

        when(objectMapper.writeValueAsString(message)).thenThrow(new JsonProcessingException("JSON error") {});

        // Act
        publisher.publishEventUpdate(message);

        // Assert
        verify(objectMapper, times(1)).writeValueAsString(message);
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void publishEventUpdate_WhenKafkaSendFails_ShouldHandleException() throws JsonProcessingException {
        // Arrange
        EventUpdateMessage message = EventUpdateMessage.builder()
                .eventId("event123")
                .currentScore("2:1")
                .timestamp(LocalDateTime.now())
                .build();

        String messageJson = "{\"eventId\":\"event123\",\"currentScore\":\"2:1\",\"timestamp\":null}";
        when(objectMapper.writeValueAsString(message)).thenReturn(messageJson);

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        // Act
        publisher.publishEventUpdate(message);

        // Assert
        verify(objectMapper, times(1)).writeValueAsString(message);
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), anyString());
    }
}
