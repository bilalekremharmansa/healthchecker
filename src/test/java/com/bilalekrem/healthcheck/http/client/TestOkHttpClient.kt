package com.bilalekrem.healthcheck.http.client

import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.client.okhttp.OkHttpClient
import com.bilalekrem.healthcheck.netty.BaseTestHttpServer
import com.bilalekrem.healthcheck.netty.server.EchoResponse
import com.bilalekrem.healthcheck.netty.server.JSONResponse
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
        map(HttpMethod.GET, "/hello", JSONResponse (
                HttpResponseStatus.OK, """{"echo":"hello"}"""
        ))

        start(port)

        // --

        val client = OkHttpClient()
        val request = HttpRequest.Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.GET)
                .build()

        val response = client.get(request)

        assertEquals(response.statusCode, HttpResponseStatus.OK.code())

        val body = response.body(Map::class)

        // -- assert

        assertEquals(body["echo"], "hello")
        assertNotEquals(body["echo"], "hola")
    }

    @Test
    fun testEchoResponse() {
        map(HttpMethod.POST, "/hello", EchoResponse())

        start(port)

        // --

        val body = JSON.toJSONNode("""{"age":10,"name":"sample-class-to-json-de/serialization"}""")

        val client = OkHttpClient()
        val request = HttpRequest.Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .body(body)
                .build()

        val response = client.get(request)

        assertEquals(response.statusCode, HttpResponseStatus.OK.code())
        assertEquals(response.body(), body)
    }

    @Test
    fun testResponseInHeader() {
        map(HttpMethod.POST, "/hello", EchoResponse())

        start(port)

        // --

        val body = JSON.toJSONNode("""{"echo":"hello"}""")

        val client = OkHttpClient()
        val request = HttpRequest.Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .body(body)
                .header("HttpClient", "OkHttp")
                .build()

        val response = client.get(request)

        assertEquals(response.statusCode, HttpResponseStatus.OK.code())
        assertEquals(response.headers.get("HttpClient"), "OkHttp")
    }

}