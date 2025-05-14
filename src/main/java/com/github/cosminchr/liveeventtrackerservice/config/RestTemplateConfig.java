package com.github.cosminchr.liveeventtrackerservice.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplateBuilder bean.
     *
     * @return The RestTemplateBuilder bean
     */
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    /**
     * Creates a RestTemplate bean.
     *
     * @param builder The RestTemplateBuilder
     * @return The RestTemplate bean
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
