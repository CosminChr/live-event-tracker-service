package com.github.cosminchr.liveeventtrackerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableScheduling
@EnableRetry
public class LiveEventTrackerServiceApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LiveEventTrackerServiceApplication.class, args);
    }

}
