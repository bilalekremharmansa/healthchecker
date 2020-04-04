package com.bilalekrem.healthcheck.http.client

import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.client.okhttp.OkHttpClient
import com.bilalekrem.healthcheck.netty.server.EchoResponse
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import com.bilalekrem.healthcheck.netty.server.JSONResponse
import com.bilalekrem.healthcheck.netty.server.MockResponse
import com.bilalekrem.healthcheck.util.JSON
import com.fasterxml.jackson.databind.JsonNode

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestOkHttpClient {

    companion object {
        private const val port = 9999
        private val server = HttpTestServer()

        fun start(port: Int) = Thread(Runnable { server.start(port) }).start()

        fun map(method: HttpMethod, endpoint: String, response: MockResponse)
                = server.map(method, endpoint, response)

        @AfterAll
        @JvmStatic
        fun closeServer() {
            server.close()
        }
    }

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

        response.body?.let {
            val body = it.toKotlinObject<Map<String, String>>()

            // -- assert

            assertEquals(body["echo"], "hello")
            assertNotEquals(body["echo"], "hola")
        } ?: assert(false) {"body should've exists"}
    }

    @Test
    fun testEchoResponse() {
        map(HttpMethod.POST, "/hello", EchoResponse())

        start(port)

        // --

        val bodyAsJsonNode = JSON.toJSONNode("""{"age":10,"name":"sample-class-to-json-de/serialization"}""")
        val requestBody = HttpBody.jsonBody(bodyAsJsonNode)

        val client = OkHttpClient()
        val request = HttpRequest.Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .body(requestBody)
                .build()

        val response = client.get(request)

        assertEquals(response.statusCode, HttpResponseStatus.OK.code())

        response.body?.let {
            val body = it.toKotlinObject<JsonNode>()
            assertEquals(bodyAsJsonNode, body)
        } ?: assert(false) {"body should've exists"}
    }

    @Test
    fun testResponseInHeader() {
        map(HttpMethod.POST, "/hello", EchoResponse())

        start(port)

        // --

        val bodyAsJsonNode = JSON.toJSONNode("""{"echo":"hello"}""")
        val requestBody = HttpBody.jsonBody(bodyAsJsonNode)

        val client = OkHttpClient()
        val request = HttpRequest.Builder()
                .uri("http://127.0.0.1:$port/hello")
                .method(com.bilalekrem.healthcheck.http.HttpMethod.POST)
                .body(requestBody)
                .header("HttpClient", "OkHttp")
                .build()

        val response = client.get(request)

        assertEquals(response.statusCode, HttpResponseStatus.OK.code())
        assertEquals(response.headers.get("HttpClient"), "OkHttp")
    }

}