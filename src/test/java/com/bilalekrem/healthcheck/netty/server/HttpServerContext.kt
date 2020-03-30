package com.bilalekrem.healthcheck.netty.server

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import java.nio.charset.Charset

class HttpServerContext {

    data class MockResponse(val status: HttpResponseStatus, val response: String, val headers: HttpHeaders? = null)

    private val mappings = hashMapOf<String, MockResponse>()

    fun map(method: HttpMethod, endpoint: String, response: MockResponse) {
        val key = getKey(method, endpoint)

        mappings[key] = response
    }

    private fun mockResponse(method: HttpMethod, endpoint: String) = mappings[getKey(method, endpoint)]

    fun responseAsNettyResponse(method: HttpMethod, endpoint: String): HttpResponse {
        val mockResponse = mockResponse(method, endpoint)

        mockResponse?.let { mock ->

            val byteArray = mock.response.toByteArray(charset = Charset.forName("UTF-8"))

            return DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    mock.status,
                    Unpooled.wrappedBuffer(byteArray))
        }

        return DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST)
    }

    private fun getKey(method: HttpMethod, endpoint: String) = "$endpoint $method"

}