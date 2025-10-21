package com.todo.first.health;

import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public CustomHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try {
            Connection connection = dataSource.getConnection();
            if (connection != null && connection.isValid(1)) {
                connection.close();
                return Health.up()
                        .withDetail("database", "MySQL connection is healthy")
                        .withDetail("status", "Available")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "MySQL connection failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
        return Health.down().withDetail("database", "Unknown error").build();
    }
}