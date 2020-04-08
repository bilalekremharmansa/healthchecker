package com.bilalekrem.healthcheck.core

import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.HttpResponse
import com.bilalekrem.healthcheck.http.client.okhttp.OkHttpClient
import com.bilalekrem.healthcheck.model.Health
import com.bilalekrem.healthcheck.model.HealthStatus

class RegexHttpHealthChecker(request: HttpRequest,
                             private val pattern: String): HttpHealthChecker(request) {

    override fun check(): Health {
        val response = client.request(request)

        // --

        val regex = pattern.toRegex()

        val healthBuilder = Health.builder()
        response.body?.let {
            val body = it.string()

            val match = regex matches body

            if (!match) {
                healthBuilder.error("pattern not matched")
            }
        } ?: healthBuilder.error("Response body is required, not found...")

        return healthBuilder.build()
    }

}