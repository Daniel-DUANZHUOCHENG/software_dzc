package edu.neu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class OaaSystemApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OaaSystemApplication.class, args);
    }
}

