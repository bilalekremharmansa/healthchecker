package com.bilalekrem.healthcheck.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestHealthBuilder {

    @DisplayName("Test creation of Health")
    @Test
    void testCreateHealthWithBuilder() {

        Health health = Health.builder()
                .status(HealthStatus.HEALTHY)
                .build();

        assert health.getStatus() == HealthStatus.HEALTHY;
    }

}
