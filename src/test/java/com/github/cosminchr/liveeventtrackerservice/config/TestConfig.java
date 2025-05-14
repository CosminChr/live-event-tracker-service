package com.github.cosminchr.liveeventtrackerservice.config;

import com.github.cosminchr.liveeventtrackerservice.dto.EventUpdateMessage;
import com.github.cosminchr.liveeventtrackerservice.service.ExternalApiService;
import com.github.cosminchr.liveeventtrackerservice.service.MessagePublisherService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return mock(RestTemplate.class);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, EventUpdateMessage> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    public ExternalApiService externalApiService() {
        return mock(ExternalApiService.class);
    }

    @Bean
    @Primary
    public MessagePublisherService messagePublisherService() {
        return mock(MessagePublisherService.class);
    }
}
