package com.bilalekrem.healthcheck.netty

import com.bilalekrem.healthcheck.core.TCPHealthChecker
import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.client.okhttp.OkHttpClient
import com.bilalekrem.healthcheck.model.HealthStatus
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import com.bilalekrem.healthcheck.netty.server.MockResponse
import com.bilalekrem.healthcheck.netty.server.StringResponse
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import kotlin.test.assertEquals

class TestHttpServer {

    companion object {
        private val server = HttpTestServer()

        fun start(port: Int) = Thread(Runnable { server.start(port) }).start()

        fun map(method: HttpMethod, endpoint: String, response: MockResponse)
                = server.map(method, endpoint, response)

        @AfterAll
        @JvmStatic
        fun closeServer() {
            server.close()
        }
    }

    @Test
    fun testInitHttpServer() {
        val port = 9999

        start(port)

        Thread.sleep(300)

        assertEquals(TCPHealthChecker("127.0.0.1", port).check().status, HealthStatus.HEALTHY)
    }

    @Test
    fun testHttpServerMap() {
        val port = 9999

        map(HttpMethod.GET, "/hello", StringResponse (
                HttpResponseStatus.ACCEPTED, "echo hello"
        ))

        start(port)

        val client = OkHttpClient()
        val request = HttpRequest.Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.GET)
                .build()

        val response = client.request(request)
        assertEquals(response.statusCode, HttpResponseStatus.ACCEPTED.code())

        response.body?.let {
            assertEquals(it.string(), "echo hello")
        } ?: assert(false) {"body should've exists"}
    }

}