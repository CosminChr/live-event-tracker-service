package com.github.cosminchr.liveeventtrackerservice.service.impl;

import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;
import com.github.cosminchr.liveeventtrackerservice.service.MessagePublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * A fallback implementation of MessagePublisherService that just logs messages.
 * This is useful for development or when Kafka is not available.
 *
 * To use this publisher, set the property: event-tracker.kafka.use-logging-publisher=true
 */
@Service("loggingMessagePublisher")
@Slf4j
@ConditionalOnProperty(name = "event-tracker.kafka.use-logging-publisher", havingValue = "true", matchIfMissing = true)
@Primary
public class LoggingMessagePublisherServiceImpl implements MessagePublisherService {

    @Override
    public void publishEventUpdate(EventUpdateMessage message) {
        log.info("MOCK PUBLISHING: Would publish event update to Kafka: message={}", message);
    }
}
