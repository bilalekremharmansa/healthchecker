package com.bilalekrem.healthcheck.service

import com.bilalekrem.healthcheck.core.HealthChecker
import com.bilalekrem.healthcheck.model.Health
import com.bilalekrem.healthcheck.model.HealthStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.logging.log4j.LogManager
import java.util.concurrent.atomic.AtomicBoolean

/*
* Kotlin coroutine implementation of HealthService
* */
class HealthServiceImpl(name: String,
                        healthChecker: HealthChecker,
                        interval: Long = 1000, // interval between health checks
                        timeout: Long = 10000, // default 10s
                        health: Health = Health.builder().build())
    : AbstractHealthService(name, healthChecker, interval, timeout, health) {

    private val logger = LogManager.getLogger()

    private var lastCheckedStatus: Long = 0
    private val allowCheck = AtomicBoolean()

    override fun start() {
        if (!allowCheck.get()) {
            allowCheck.set(true)

            checkContinuously()
        }
    }

    override fun stop() {
        allowCheck.set(false)
    }

    override fun status(): HealthStatus = health.status

    private fun checkContinuously() {
        GlobalScope.launch {
            while (allowCheck.get()) {
                val result = withTimeoutOrNull(timeout) {
                    val health = healthChecker.check()

                    updateHealth(health)

                    true
                }

                if (result == null) {
                    logger.warn("timeout while health checking")
                }

                logger.trace("Health status of[$name]: ${health.status} [$lastCheckedStatus]")
                delay(interval)
            }

            // set unknown before exiting..
            updateHealth(Health.builder().build())
        }
    }

    private fun updateHealth(health: Health) {
        this.health = health
        lastCheckedStatus = System.currentTimeMillis()
    }

}
