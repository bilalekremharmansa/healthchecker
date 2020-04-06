package com.bilalekrem.healthcheck.service

import com.bilalekrem.healthcheck.core.TCPHealthChecker
import com.bilalekrem.healthcheck.model.HealthStatus
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestHealthService {

    val ip = "127.0.0.1"
    val port = 9999

    @Test
    fun testHealthService() {

        val service = HealthServiceImpl("test", TCPHealthChecker(ip, port))

        val service2 = HealthServiceImpl("test2", TCPHealthChecker(ip, port))

        assertEquals(service.status(), HealthStatus.UNKNOWN)

        service.start()
        service2.start()

        val server = HttpTestServer().apply {
            start(9999)
        }
        Thread.sleep(1000)
        assertEquals(service.status(), HealthStatus.HEALTHY)

        server.stop()
        Thread.sleep(1000)
        assertEquals(service.status(), HealthStatus.UNHEALTHY)

        service.stop()
        service2.stop()
    }
}