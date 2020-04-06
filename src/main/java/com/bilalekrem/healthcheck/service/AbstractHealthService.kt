package com.bilalekrem.healthcheck.service

import com.bilalekrem.healthcheck.core.HealthChecker
import com.bilalekrem.healthcheck.model.Health

abstract class AbstractHealthService(protected val name: String,
                                     protected val healthChecker: HealthChecker,
                                     protected val interval: Long = 1000, // interval between health checks
                                     protected val timeout: Long = 10000, // default 10s
                                     protected var health: Health = Health.builder().build()): HealthService {

    override fun health(): Health = health

}