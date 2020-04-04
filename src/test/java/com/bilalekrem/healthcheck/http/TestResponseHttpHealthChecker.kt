package com.bilalekrem.healthcheck.http

import com.bilalekrem.healthcheck.core.ResponseHttpHealthChecker
import com.bilalekrem.healthcheck.http.client.HttpBody
import com.bilalekrem.healthcheck.model.HealthStatus
import com.bilalekrem.healthcheck.model.TestGreetingObject
import com.bilalekrem.healthcheck.netty.server.EchoResponse
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import com.bilalekrem.healthcheck.netty.server.JSONResponse
import io.netty.handler.codec.http.HttpMethod as NettyHttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestResponseHttpHealthChecker {

    private val logger = LogManager.getLogger()

    companion object {

        private val server = HttpTestServer()

        const val PORT = 9999
        const val BASE_URL = "http://127.0.0.1:$PORT"

        @JvmStatic
        @BeforeAll
        fun initServer() {
            server.start(PORT)

            server.map(NettyHttpMethod.GET, "/forSuccess", JSONResponse(
                    HttpResponseStatus.OK, """{"message": "it is a success" }""")
            )

            server.map(NettyHttpMethod.GET, "/echo", EchoResponse())
            server.map(NettyHttpMethod.POST, "/echo", EchoResponse())
        }

        @AfterAll
        @JvmStatic
        fun closeServer() {
            server.close()
        }

    }

    @Test
    fun testSuccess() {
        val request = HttpRequest
                .Builder()
                .uri("$BASE_URL/forSuccess")
                .method(HttpMethod.GET)
                .build()

        val expectedBody = HttpBody.objectToJsonBody(TestGreetingObject("it is a success"))
        val expectedResponse = HttpResponse(200, expectedBody)

        val checker = ResponseHttpHealthChecker(request, expectedResponse)

        // --

        assertEquals(checker.check().status, HealthStatus.HEALTHY)
    }

    @Test
    fun testFailForStatusCode() {
        val request = HttpRequest
                .Builder()
                .uri("$BASE_URL/forSuccess")
                .method(HttpMethod.GET)
                .build()

        val expectedBody = HttpBody.objectToJsonBody(TestGreetingObject("it is a success"))
        val expectedResponse = HttpResponse(201, expectedBody)

        val checker = ResponseHttpHealthChecker(request, expectedResponse)

        val health = checker.check()

        // --

        assertEquals(health.status, HealthStatus.UNHEALTHY)

        assertEquals(health.errors.size, 1)

        val errorMessage = health.errors.first()?.message

        logger.info("error message: {}", errorMessage)

        assert(errorMessage?.contains("status code are not exact")!!)
    }

    @Test
    fun testFailForHeader() {
        val body = HttpBody.objectToJsonBody(TestGreetingObject("it is a success"))

        val request = HttpRequest
                .Builder()
                .uri("$BASE_URL/echo")
                .method(HttpMethod.POST)
                .header("test-header", "for-success")
                .body(body)
                .build()

        val expectedResponse = HttpResponse(200, body,
                HttpHeaders().apply {
                    add("test-header", "for-fail")
                    add("missing-header", "fail") })

        val checker = ResponseHttpHealthChecker(request, expectedResponse)

        val health = checker.check()

        // --

        assertEquals(health.status, HealthStatus.UNHEALTHY)
        assertEquals(health.errors.size, 2)

        health.errors.forEach {
            logger.info("error message: {}", it.message)
            assert(it.message.contains("header"))
        }
    }


    @Test
    fun testFailForBody() {
        val request = HttpRequest
                .Builder()
                .uri("$BASE_URL/forSuccess")
                .method(HttpMethod.GET)
                .build()

        val expectedBody = HttpBody.objectToJsonBody(TestGreetingObject("it is a fail"))
        val expectedResponse = HttpResponse(200, expectedBody)

        val checker = ResponseHttpHealthChecker(request, expectedResponse)

        val health = checker.check()

        // --

        assertEquals(health.status, HealthStatus.UNHEALTHY)
        assertEquals(health.errors.size, 1)

        health.errors.forEach {
            logger.info("error message: {}", it.message)
            assert(it.message.contains("response bodies are not matching"))
        }
    }


}