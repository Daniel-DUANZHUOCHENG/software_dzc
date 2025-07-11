package edu.neu.oaas.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@TestConfiguration
public class UserBehaviorTestConfig {
    
    @Bean
    public Resource visitorCounterFile() {
        return new ClassPathResource("visitor-counter-test.txt");
    }
} 