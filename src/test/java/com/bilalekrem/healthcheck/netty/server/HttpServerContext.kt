package com.bilalekrem.healthcheck.netty.server

import io.netty.handler.codec.http.*
import java.lang.RuntimeException

class HttpServerContext {

    class MappingNotFoundException(message: String) : RuntimeException(message)

    private val mappings = hashMapOf<String, MockResponse>()

    fun map(method: HttpMethod, endpoint: String, response: MockResponse) {
        val key = getKey(method, endpoint)

        mappings[key] = response
    }

    private fun mockResponse(method: HttpMethod, endpoint: String) = mappings[getKey(method, endpoint)]

    fun responseAsNettyResponse(request: FullHttpRequest): FullHttpResponse {
        val method = request.method()
        val uri = request.uri()

        val mockResponse = mockResponse(method, uri)

        return mockResponse?.response(request) ?: throw MappingNotFoundException("[$method] mapping not found: [$uri]")
    }

    private fun getKey(method: HttpMethod, endpoint: String) = "$endpoint $method"

}