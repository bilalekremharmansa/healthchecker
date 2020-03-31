package com.bilalekrem.healthcheck.http

import com.bilalekrem.healthcheck.http.client.okhttp.OkHttpClient
import com.bilalekrem.healthcheck.netty.BaseTestHttpServer
import com.bilalekrem.healthcheck.netty.server.HttpServerContext

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestOkHttpClient: BaseTestHttpServer() {

    private val port = 9999

    @Test
    fun testGetMethod() {
        map(HttpMethod.GET, "/hello", HttpServerContext.MockResponse(
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
    fun testPostMethod() {
        map(HttpMethod.GET, "/hello", HttpServerContext.MockResponse(
                HttpResponseStatus.OK, """{"echo":"hello"}"""
        ))

        start(port)

        // --

        val client = OkHttpClient()
        val request = HttpRequest
                .Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .build()

        val response = client.get<Map<String, String>>(request)

        assert(response.statusCode !in 200..399)
    }



}