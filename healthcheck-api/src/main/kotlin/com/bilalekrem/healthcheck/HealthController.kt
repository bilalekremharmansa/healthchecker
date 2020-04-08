package com.bilalekrem.healthcheck

import com.bilalekrem.healthcheck.configuration.ServiceDefinitions
import com.bilalekrem.healthcheck.service.HealthService
import com.bilalekrem.healthcheck.service.HealthServiceImpl
import org.apache.logging.log4j.LogManager
import java.lang.RuntimeException

class HealthController {

    private val logger = LogManager.getLogger()
    private val services = mutableMapOf<String, HealthService>()

    fun create(service: ServiceDefinitions, startOnCreate: Boolean = false) {
        services[service.name]
                ?.let {
                    logger.error("service already defined: [$service.name]")
                    throw RuntimeException("service already defined: [$service.name]")
                }
                ?: run {
                    services[service.name] = HealthServiceImpl(service.name, service.checker.createChecker(),
                            service.interval, service.timeout)
                }
        if (startOnCreate) {
            services[service.name]?.start()
        }
    }

    fun destroy(name: String) {
        services[name]?.let { services.remove(name) } ?: throw RuntimeException("service not found: [$name]")
    }

    fun start(name: String) = services[name]?.start() ?: throw RuntimeException("service not found: [$name]")

    fun stop(name: String) = services[name]?.stop() ?: throw RuntimeException("service not found: [$name]")

    fun status(name: String) = services[name]?.status() ?: throw RuntimeException("service not found: [$name]")

}