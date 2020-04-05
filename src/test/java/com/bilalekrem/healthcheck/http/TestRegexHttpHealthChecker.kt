package com.bilalekrem.healthcheck.http

import com.bilalekrem.healthcheck.core.RegexHttpHealthChecker
import com.bilalekrem.healthcheck.core.ResponseHttpHealthChecker
import com.bilalekrem.healthcheck.http.client.HttpBody
import com.bilalekrem.healthcheck.model.HealthStatus
import com.bilalekrem.healthcheck.model.TestGreetingObject
import com.bilalekrem.healthcheck.netty.server.EchoResponse
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import com.bilalekrem.healthcheck.netty.server.JSONResponse
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestRegexHttpHealthChecker {

    companion object {

        private val server = HttpTestServer()

        const val PORT = 9999
        const val BASE_URL = "http://127.0.0.1:$PORT"

        @JvmStatic
        @BeforeAll
        fun initServer() {
            server.start(PORT)

            server.map(HttpMethod.GET, "/echo", EchoResponse())
            server.map(HttpMethod.POST, "/echo", EchoResponse())
        }

        @AfterAll
        @JvmStatic
        fun closeServer() {
            server.close()
        }

    }

    @Test
    fun testSuccess() {
        val body = HttpBody.objectToJsonBody(TestGreetingObject("it is a success"))

        val request = HttpRequest
                .Builder()
                .uri("${TestResponseHttpHealthChecker.BASE_URL}/echo")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .body(body)
                .build()


        val checker = RegexHttpHealthChecker(request, ".*success.*")

        // --

        assertEquals(checker.check().status, HealthStatus.HEALTHY)
    }

    @Test
    fun testFail() {
        val body = HttpBody.objectToJsonBody(TestGreetingObject("it is a success"))

        val request = HttpRequest
                .Builder()
                .uri("${TestResponseHttpHealthChecker.BASE_URL}/echo")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .body(body)
                .build()

        val checker = RegexHttpHealthChecker(request, ".*fail.*")
        val health = checker.check()
        // --

        assertEquals(health.status, HealthStatus.UNHEALTHY)

        assert(health.errors[0].message.contains("pattern not matched"))
    }

    @Test
    fun testFailForEmptyBody() {
        val request = HttpRequest
                .Builder()
                .uri("${TestResponseHttpHealthChecker.BASE_URL}/echo")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .build()

        val checker = RegexHttpHealthChecker(request, ".*fail.*")
        val health = checker.check()
        // --

        assertEquals(health.status, HealthStatus.UNHEALTHY)

        assert(health.errors[0].message.contains("body is required"))
    }

}