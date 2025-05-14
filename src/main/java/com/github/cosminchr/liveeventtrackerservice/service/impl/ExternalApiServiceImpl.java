package com.github.cosminchr.liveeventtrackerservice.service.impl;

import com.github.cosminchr.liveeventtrackerservice.dto.EventApiResponse;
import com.github.cosminchr.liveeventtrackerservice.service.ExternalApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

/**
 * Implementation of the ExternalApiService interface.
 */
@Service
@Slf4j
public class ExternalApiServiceImpl implements ExternalApiService {

    @Value("${event-tracker.external-api.base-url}")
    private String baseUrl;

    @Value("${event-tracker.external-api.event-endpoint}")
    private String eventEndpoint;

    private final RestTemplate restTemplate;

    public ExternalApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        log.info("Initialized ExternalApiServiceImpl with RestTemplate");
    }

    @Override
    @Retryable(
            value = {RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public EventApiResponse fetchEventData(String eventId) {
        String url = baseUrl + eventEndpoint.replace("{eventId}", eventId);

        log.debug("Fetching event data from external API: url={}, eventId={}", url, eventId);

        try {
            EventApiResponse response = restTemplate.getForObject(url, EventApiResponse.class);
            log.debug("Received response from external API: eventId={}, response={}", eventId, response);
            return response;
        } catch (RestClientException e) {
            log.error("Error fetching event data from external API: eventId={}, error={}", eventId, e.getMessage());
            throw e;
        }
    }
}
