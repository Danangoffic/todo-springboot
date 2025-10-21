package com.todo.first.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ActuatorConfig {

    @Bean
    public InfoContributor appInfoContributor(Environment environment) {
        return builder -> {
            Map<String, Object> details = new HashMap<>();
            details.put("app", "Todo Application");
            details.put("version", "1.0.0");
            details.put("profiles", environment.getActiveProfiles());
            details.put("java.version", System.getProperty("java.version"));
            details.put("spring.boot.version", getClass().getPackage().getImplementationVersion());

            builder.withDetails(details);
        };
    }
}