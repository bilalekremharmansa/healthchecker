package com.bilalekrem.healthcheck.checker

import com.bilalekrem.healthcheck.core.ResponseHttpHealthChecker
import com.bilalekrem.healthcheck.http.HttpHeaders
import com.bilalekrem.healthcheck.http.HttpMethod
import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.HttpResponse
import com.bilalekrem.healthcheck.http.client.HttpBody
import com.bilalekrem.healthcheck.model.HealthStatus
import com.bilalekrem.healthcheck.model.TestGreetingObject

import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.Test

import kotlin.test.assertEquals

class TestResponseHttpHealthChecker: BaseTestHttpHealthChecker() {

    private val logger = LogManager.getLogger()

    @Test
    fun testSuccess() {
        val request = HttpRequest.Builder()
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
        val request = HttpRequest.Builder()
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

        val request = HttpRequest.Builder()
                .uri("$BASE_URL/echo")
                .method(HttpMethod.POST)
                .header("test-header", "for-success")
                .body(body)
                .build()

        val expectedResponse = HttpResponse(200, body,
                HttpHeaders().apply {
                    add("test-header", "for-fail")
                    add("missing-header", "fail")
                })

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
        val request = HttpRequest.Builder()
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