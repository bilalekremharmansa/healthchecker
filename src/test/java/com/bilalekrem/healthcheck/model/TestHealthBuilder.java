package com.bilalekrem.healthcheck.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestHealthBuilder {

    private static Logger logger = LogManager.getLogger();

    @DisplayName("Test creation of Health")
    @Test
    void testCreateHealthWithBuilder() {
        logger.info("Starting health creation test");

        Health health = Health.builder()
                .status(HealthStatus.HEALTHY)
                .build();

        assert health.getStatus() == HealthStatus.HEALTHY;
    }

}
