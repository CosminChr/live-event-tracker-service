package com.github.cosminchr.liveeventtrackerservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;
import com.github.cosminchr.liveeventtrackerservice.service.MessagePublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Simple implementation of the MessagePublisherService interface using Kafka with String serialization.
 */
@Service("simpleKafkaMessagePublisher")
@Slf4j
@ConditionalOnProperty(name = "event-tracker.kafka.use-logging-publisher", havingValue = "false", matchIfMissing = false)
public class SimpleKafkaMessagePublisherServiceImpl implements MessagePublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${event-tracker.kafka.event-updates-topic:event-updates}")
    private String eventUpdatesTopic;

    public SimpleKafkaMessagePublisherServiceImpl(KafkaTemplate<String, String> stringKafkaTemplate) {
        this.kafkaTemplate = stringKafkaTemplate;
        this.objectMapper = new ObjectMapper();
        // Register the JSR310 module to handle Java 8 date/time types
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        // Configure to use ISO-8601 dates
        this.objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void publishEventUpdate(EventUpdateMessage message) {
        String key = message.getEventId();

        try {
            // Convert the message to a JSON string
            String messageJson = objectMapper.writeValueAsString(message);

            log.info("Publishing event update to Kafka: topic={}, key={}, message={}",
                    eventUpdatesTopic, key, messageJson);

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(eventUpdatesTopic, key, messageJson);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event update published successfully: topic={}, key={}, offset={}",
                            eventUpdatesTopic, key, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event update: topic={}, key={}, error={}",
                            eventUpdatesTopic, key, ex.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing message to JSON: key={}, error={}", key, e.getMessage());
        } catch (Exception e) {
            log.error("Error publishing event update: topic={}, key={}, error={}",
                    eventUpdatesTopic, key, e.getMessage());
            throw e;
        }
    }
}
