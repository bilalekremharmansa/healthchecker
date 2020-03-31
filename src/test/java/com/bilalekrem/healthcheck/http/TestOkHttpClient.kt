package com.bilalekrem.healthcheck.http

import com.bilalekrem.healthcheck.http.client.okhttp.OkHttpClient
import com.bilalekrem.healthcheck.netty.BaseTestHttpServer
import com.bilalekrem.healthcheck.netty.server.EchoResponse
import com.bilalekrem.healthcheck.netty.server.StringResponse
import com.bilalekrem.healthcheck.util.JSON

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestOkHttpClient: BaseTestHttpServer() {

    private val port = 9999

    @Test
    fun testGetMethod() {
        map(HttpMethod.GET, "/hello", StringResponse (
                HttpResponseStatus.OK, """{"echo":"hello"}"""
        ))

        start(port)

        // --

        val client = OkHttpClient()
        val request = HttpRequest
                .Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.GET)
                .build()

        val response = client.get<Map<String, String>>(request)

        assert(response.statusCode in 200..399)

        val body = response.body

        // -- assert

        body?.let {

            assertEquals(body["echo"], "hello")
            assertNotEquals(body["echo"], "hola")
        } ?: assert(false)
    }

    @Test
    fun testEchoResponse() {
        map(HttpMethod.POST, "/hello", EchoResponse())

        start(port)

        // --

        val body = """{"echo":"hello"}"""

        val client = OkHttpClient()
        val request = HttpRequest
                .Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .body(body)
                .build()

        val response: HttpResponse<Map<String, String>> = client.get(request)

        assert(response.statusCode in 200..399)
        assertEquals(response.body.toString(), body)
    }

}