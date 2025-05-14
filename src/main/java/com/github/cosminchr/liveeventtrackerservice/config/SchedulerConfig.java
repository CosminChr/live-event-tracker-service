package com.github.cosminchr.liveeventtrackerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration for the task scheduler.
 */
@Configuration
public class SchedulerConfig {

    /**
     * Creates a TaskScheduler bean.
     *
     * @return The TaskScheduler bean
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("task-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
