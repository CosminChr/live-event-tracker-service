package com.github.cosminchr.liveeventtrackerservice.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;
import com.github.cosminchr.liveeventtrackerservice.service.impl.LoggingMessagePublisherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoggingMessagePublisherServiceTest {

    private LoggingMessagePublisherServiceImpl loggingMessagePublisher;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        loggingMessagePublisher = new LoggingMessagePublisherServiceImpl();

        // Set up logger to capture log messages
        logger = (Logger) LoggerFactory.getLogger(LoggingMessagePublisherServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void publishEventUpdate_ShouldLogMessage() {
        // Arrange
        EventUpdateMessage message = EventUpdateMessage.builder()
                .eventId("event123")
                .currentScore("2:1")
                .timestamp(LocalDateTime.now())
                .build();

        // Act
        loggingMessagePublisher.publishEventUpdate(message);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent loggingEvent = listAppender.list.get(0);

        assertEquals(Level.INFO, loggingEvent.getLevel());
        assertTrue(loggingEvent.getFormattedMessage().contains("MOCK PUBLISHING"));
        assertTrue(loggingEvent.getFormattedMessage().contains("event123"));
        // EventUpdateMessage doesn't have an eventName field
        assertTrue(loggingEvent.getFormattedMessage().contains("2:1"));
    }
}
