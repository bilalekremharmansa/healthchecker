package com.bilalekrem.healthcheck.model;

public class Health {

    private HealthStatus status;

    private Health(HealthStatus status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public HealthStatus getStatus() {
        return status;
    }

    public void setStatus(HealthStatus status) {
        this.status = status;
    }

    // -- builder

    public static class Builder {
        private HealthStatus status;



        public Builder status(HealthStatus status) {
            this.status = status;

            return this;
        }

        public Health build() {
            return new Health(this.status);
        }

    }
}
