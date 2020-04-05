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

class HealthContext(val name: String,
                    private val healthChecker: HealthChecker,
                    private val interval: Long = 1000, // interval between health checks
                    private val timeout: Long = 10000, // default 10s
                    var health: Health = Health.builder().build()) {

    private val logger = LogManager.getLogger()

    private var lastCheckedStatus: Long = 0
    private val allowCheck = AtomicBoolean()

    fun start() {
        allowCheck.set(true)

        checkContinuously()
    }

    fun stop() {
        allowCheck.set(false)
    }

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

                println("[$name] ${health.status} $lastCheckedStatus")
                delay(interval)
            }
        }
    }

    fun status(): HealthStatus = health.status

    private fun updateHealth(health: Health) {
        this.health = health
        lastCheckedStatus = System.currentTimeMillis()
    }

}
