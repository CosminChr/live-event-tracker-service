package com.github.cosminchr.liveeventtrackerservice.config;

import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.github.cosminchr.liveeventtrackerservice.config.SimpleJsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Kafka.
 * Only active when the logging publisher is not in use.
 */
@Configuration
@ConditionalOnProperty(name = "event-tracker.kafka.use-logging-publisher", havingValue = "false", matchIfMissing = false)
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${event-tracker.kafka.event-updates-topic:event-updates}")
    private String eventUpdatesTopic;

    /**
     * Creates a KafkaAdmin bean for managing Kafka topics.
     *
     * @return The KafkaAdmin bean
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * Creates the event updates topic.
     *
     * @return The NewTopic bean
     */
    @Bean
    public NewTopic eventUpdatesTopic() {
        return TopicBuilder.name(eventUpdatesTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Creates a producer factory for EventUpdateMessage.
     *
     * @return The ProducerFactory bean
     */
    @Bean
    public ProducerFactory<String, EventUpdateMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SimpleJsonSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        // Explicitly disable idempotence to work with older Kafka versions
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a KafkaTemplate for EventUpdateMessage.
     *
     * @return The KafkaTemplate bean
     */
    @Bean
    public KafkaTemplate<String, EventUpdateMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Creates a producer factory for String messages.
     *
     * @return The ProducerFactory bean
     */
    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        // Explicitly disable idempotence to work with older Kafka versions
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a KafkaTemplate for String messages.
     *
     * @return The KafkaTemplate bean
     */
    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }
}
