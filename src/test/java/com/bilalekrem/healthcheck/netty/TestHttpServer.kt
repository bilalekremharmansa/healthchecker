package com.bilalekrem.healthcheck.netty

import com.bilalekrem.healthcheck.core.TCPHealthChecker
import com.bilalekrem.healthcheck.model.HealthStatus
import com.bilalekrem.healthcheck.netty.server.HttpServerContext
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestHttpServer {

    @Test
    fun testInitHttpServer() {
        val port = 9999

        val server = HttpTestServer(port)

        serverThread(server)
                .start()

        Thread.sleep(3000)

        assertEquals(TCPHealthChecker("127.0.0.1", port).check().status, HealthStatus.HEALTHY)

        Thread.sleep(3000)

        server.stop()
    }

    @Test
    fun testHttpServerMap() {
        val port = 9999

        val server = HttpTestServer(port)

        serverThread(server)
                .start()

        server.map(HttpMethod.GET, "/hello", HttpServerContext.MockResponse(
                HttpResponseStatus.OK, "echo hello"
        ))

        Thread.sleep(10000)

        server.stop()
    }

    fun serverThread(server: HttpTestServer): Thread = Thread(Runnable { server.start() })

}