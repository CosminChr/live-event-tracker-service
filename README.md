# Live Event Tracker Service

A Java-based microservice that tracks live sports events, periodically polls an external API for updates, and publishes the data to a message broker.

## Overview

This service provides the following functionality:

- REST endpoint to receive event status updates (live ↔ not live)
- For each live event, a scheduled task polls an external API every 10 seconds
- Transforms API responses into messages and publishes them to Kafka
- Implements error handling, retries, and logging

## Technologies Used

- Java 21
- Spring Boot 3.4.5
- Spring Kafka
- Spring Retry
- Lombok
- JUnit 5

## Setup & Run Instructions

### Prerequisites

- Java 21 or higher
- Gradle
- Kafka (optional, the application can run without it)

### Running the Application

```bash
# Run the application
./gradlew bootRun
```

By default, the application uses a logging message publisher instead of actually publishing to Kafka. This is the simplest way to run the application as it doesn't require Kafka to be running.

If you want to use Kafka, you need to set the `event-tracker.kafka.use-logging-publisher` property to `false` in the application.yml file or as a command-line argument:

```bash
# Run the application with Kafka
./gradlew bootRun --args='--event-tracker.kafka.use-logging-publisher=false'
```

### Building the Application

```bash
./gradlew clean build
```

### Running the Application

#### Option 1: Using Gradle (recommended for development)

```bash
./gradlew bootRun
```

#### Using the executable JAR file

```bash
# Build the application first
./gradlew build

# Run the JAR (uses logging publisher by default)
java -jar build/libs/live-event-tracker-service-0.0.1-SNAPSHOT.jar

# OR run with Kafka
java -jar build/libs/live-event-tracker-service-0.0.1-SNAPSHOT.jar --event-tracker.kafka.use-logging-publisher=false
```

## API Documentation

### Update Event Status

```
POST /api/events/status
```

Request body:

```json
{
  "eventId": "1234",
  "live": true
}
```

Response:

```json
{
  "eventId": "1234",
  "status": "LIVE",
  "currentScore": null,
  "lastUpdated": "2023-06-01T12:34:56.789",
  "lastPolled": null
}
```

### Mock External API (for testing)

```
GET /api/mock/events/{eventId}
```

Response:

```json
{
  "eventId": "1234",
  "currentScore": "1:0"
}
```

## Testing

### Running Tests

```bash
./gradlew test
```

### Manual Testing

1. Start the application
2. Set an event to "live" status:

```bash
curl -X POST http://localhost:8080/api/events/status \
  -H "Content-Type: application/json" \
  -d '{"eventId":"1234","live":true}'
```

3. Check the logs to see the polling and message publishing
4. Set the event to "not live" status:

```bash
curl -X POST http://localhost:8080/api/events/status \
  -H "Content-Type: application/json" \
  -d '{"eventId":"1234","live":false}'
```

## Design Decisions

### In-Memory Storage

For simplicity, this implementation uses in-memory storage for events. In a production environment, a database would be used to persist event data.

### Scheduler Implementation

The service uses Spring's `@Scheduled` annotation to run a task every 10 seconds that checks all live events. This approach was chosen for simplicity and reliability.

### Error Handling

The service implements retry logic for both external API calls and Kafka message publishing using Spring Retry. This ensures that transient failures don't cause data loss.

### Kafka Integration

Kafka was chosen as the message broker for its scalability and reliability. The service creates a dedicated topic for event updates and uses Spring Kafka for integration.

## AI-Assisted Development

This project was developed with the assistance of AI tools. The following parts were AI-generated:

- Initial project structure and boilerplate code
- Service interfaces and implementations
- Scheduler implementation
- Kafka configuration

All AI-generated code was reviewed, tested, and modified as needed to ensure it meets the requirements and follows best practices.

## Future Improvements

- Add a database for persistent storage
- Implement authentication and authorization
- Add more comprehensive metrics and monitoring
- Create a UI for visualizing live events
- Implement websockets for real-time updates to clients
