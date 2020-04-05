package com.bilalekrem.healthcheck.model;

import java.util.ArrayList;
import java.util.List;

public class Health {

    private HealthStatus status;
    private List<Error> errors;

    private Health(HealthStatus status, List<Error> errors) {
        this.status = status;
        this.errors = errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public HealthStatus getStatus() {
        return status;
    }

    public List<Error> getErrors() {
        return errors;
    }

    // -- builder

    public static class Builder {
        private HealthStatus status = HealthStatus.UNKNOWN;
        private List<Error> errors = new ArrayList<>(3);

        public Builder status(HealthStatus status) {
            this.status = status;

            return this;
        }

        public Builder error(String message) {
            errors.add(new Error(message));

            return this;
        }

        public Health build() {
            if (status == null) {
                status = errors.size() == 0 ? HealthStatus.HEALTHY: HealthStatus.UNHEALTHY;
            }

            if (status == HealthStatus.HEALTHY && errors.size() > 0) {
                throw new IllegalStateException("status not be healthy while having existing errors");
            }

            return new Health(status, errors);
        }

    }
}
