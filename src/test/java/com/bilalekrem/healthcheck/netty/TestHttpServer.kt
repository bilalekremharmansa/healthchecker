package com.bilalekrem.healthcheck.netty

import com.bilalekrem.healthcheck.core.TCPHealthChecker
import com.bilalekrem.healthcheck.model.HealthStatus
import com.bilalekrem.healthcheck.netty.server.StringResponse
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestHttpServer: BaseTestHttpServer() {

    @Test
    fun testInitHttpServer() {
        val port = 9999

        start(port)

        Thread.sleep(3000)

        assertEquals(TCPHealthChecker("127.0.0.1", port).check().status, HealthStatus.HEALTHY)

        Thread.sleep(3000)
    }

    @Test
    fun testHttpServerMap() {
        val port = 9999

        map(HttpMethod.GET, "/hello", StringResponse (
                HttpResponseStatus.OK, "echo hello"
        ))

        start(port)

        Thread.sleep(10000)
    }

}