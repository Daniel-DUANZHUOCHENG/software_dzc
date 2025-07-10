package edu.neu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"edu.neu", "edu.neu.oaas"})
public class OaaSystemApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OaaSystemApplication.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OaaSystemApplication.class);
    }
}

