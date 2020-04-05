package com.bilalekrem.healthcheck.checker

import com.bilalekrem.healthcheck.core.RegexHttpHealthChecker
import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.client.HttpBody
import com.bilalekrem.healthcheck.model.HealthStatus
import com.bilalekrem.healthcheck.model.TestGreetingObject
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestRegexHttpHealthChecker: BaseTestHttpHealthChecker() {

    @Test
    fun testSuccess() {
        val body = HttpBody.objectToJsonBody(TestGreetingObject("it is a success"))

        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/echo")
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

        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/echo")
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
        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/echo")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .build()

        val checker = RegexHttpHealthChecker(request, ".*fail.*")
        val health = checker.check()
        // --

        assertEquals(health.status, HealthStatus.UNHEALTHY)

        assert(health.errors[0].message.contains("body is required"))
    }

}