package com.bilalekrem.healthcheck.configuration

import com.bilalekrem.healthcheck.core.HealthChecker
import com.bilalekrem.healthcheck.core.RegexHttpHealthChecker
import com.bilalekrem.healthcheck.core.TCPHealthChecker
import com.bilalekrem.healthcheck.http.HttpMethod
import com.bilalekrem.healthcheck.http.HttpRequest
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class ConfigurationWrapper(
    val definitions: List<ServiceDefinitions> = listOf()
)

data class ServiceDefinitions(
        val name: String,
        val interval: Long = 1000,
        val timeout: Long = 1000,
        val checker: HealthCheckerProperties<HealthChecker>
)

// -- Base health checker

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
abstract class HealthCheckerProperties<T: HealthChecker>() {

    abstract fun createChecker(): T

}

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
        AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class HealthCheckerConfiguration(val typeName: String)

// -- TCP Health Checker

@HealthCheckerConfiguration(typeName = "tcp")
class TCPHealthCheckerProperties (private val ip: String,
                                  private val port: Int,
                                  private val timeout: Int? = null): HealthCheckerProperties<TCPHealthChecker>() {

    override fun createChecker(): TCPHealthChecker {
        return timeout?.let { TCPHealthChecker(ip, port, timeout)  } ?: TCPHealthChecker(ip, port)
    }

}

// --- Http Health Checker

class HttpRequestProperties(val uri: String,
                            val method: HttpMethod,
                            val headers: List<HttpHeaderProperties>)

class HttpHeaderProperties(val name: String, val value: String)

@HealthCheckerConfiguration(typeName = "regex")
class RegexHttpHealthCheckerProperties(private val request: HttpRequestProperties,
                                       private val pattern: String): HealthCheckerProperties<RegexHttpHealthChecker>() {

    override fun createChecker(): RegexHttpHealthChecker {
        val builder = HttpRequest
                .Builder()
                .uri(request.uri)
                .method(request.method)

        request.headers.forEach { builder.header(it.name, it.value) }

        return RegexHttpHealthChecker(builder.build(), pattern)
    }

}
