package com.bilalekrem.healthcheck.http.client

import com.bilalekrem.healthcheck.http.HttpMethod
import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.RequestBodyIsNotPermittedException
import com.bilalekrem.healthcheck.netty.TestHttpServer
import com.bilalekrem.healthcheck.netty.server.EchoResponse
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import com.bilalekrem.healthcheck.netty.server.JSONResponse
import com.bilalekrem.healthcheck.netty.server.StringResponse
import com.bilalekrem.healthcheck.util.JSON

import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpMethod as NettyHttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.*

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTestHttpClient(protected val client: HttpClient) {


    private val PORT = 9999
    protected val BASE_URL = "http://127.0.0.1:${PORT}"
    private val server = HttpTestServer()

    @BeforeAll
    fun initServer() {
        server.start(PORT)

        server.map(NettyHttpMethod.GET, "/forSuccess", JSONResponse(
                HttpResponseStatus.OK, """{"message": "it is a success" }""",
                DefaultHttpHeaders()
                    .add("result", "success")
                    .add("message-exists", "true"))
        )

        TestHttpServer.map(NettyHttpMethod.GET, "/hello", StringResponse(
                HttpResponseStatus.OK, "echo hello"
        ))

        server.map(NettyHttpMethod.GET, "/echo", EchoResponse())
        server.map(NettyHttpMethod.POST, "/echo", EchoResponse())
    }

    @AfterAll
    fun closeServer() {
        server.close()
    }

    // -- GET

    @Test
    fun `GET - check status code is ok`() {
        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/forSuccess")
                .method(HttpMethod.GET)
                .build()

        val response = client.get(request)

        assertEquals(response.statusCode, HttpResponseStatus.OK.code())
    }

    @Test
    fun `GET - check body`() {
        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/forSuccess")
                .method(HttpMethod.GET)
                .build()

        val response = client.get(request)

        response.body?.let {
            val body = it.jsonToObject<Map<String, String>>()

            // -- assert

            assertEquals(body["message"], "it is a success")
            assertNotEquals(body["message"], "it is a fail ?")
        } ?: assert(false) {"body should've exists"}
    }

    @Test
    fun `GET - check headers`() {
        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/forSuccess")
                .method(HttpMethod.GET)
                .build()

        val response = client.get(request)

        assertEquals(response.headers.get("result"), "success")
        assertEquals(response.headers.get("message-exists"), "true")

        assertNull(response.headers.get("header-is-not-exists"))
    }

    @Test
    fun `GET - request can not have a body`() {
        val bodyAsJsonNode = JSON.toJSONNode("""{"echo":"hello"}""")
        val requestBody = HttpBody.objectToJsonBody(bodyAsJsonNode)

        val builder = HttpRequest.Builder()
                .uri("${BASE_URL}/echo")
                .method(HttpMethod.GET)
                .body(requestBody)
                .header("HttpClient", "OkHttp")

        assertThrows<RequestBodyIsNotPermittedException> {  builder.build() }
    }

    // -- POST

    @Test
    fun `POST - check status code`() {
        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/forSuccess")
                .method(HttpMethod.GET)
                .build()

        val response = client.get(request)

        assertEquals(response.statusCode, HttpResponseStatus.OK.code())
    }

    @Test
    fun `POST - check body`() {
        val bodyAsJsonNode = JSON.toJSONNode("""{"message":"hello"}""")
        val requestBody = HttpBody.objectToJsonBody(bodyAsJsonNode)

        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/echo")
                .method(HttpMethod.POST)
                .body(requestBody)
                .build()

        val response = client.get(request)

        response.body?.let {
            val body = it.jsonToObject<Map<String, String>>()

            // -- assert

            assertEquals(body["message"], "hello")
            assertNotEquals(body["message"], "hola ?")
        } ?: assert(false) {"body should've exists"}
    }

    @Test
    fun `POST - check headers`() {
        val bodyAsJsonNode = JSON.toJSONNode("""{"message":"hello"}""")
        val requestBody = HttpBody.objectToJsonBody(bodyAsJsonNode)

        val request = HttpRequest.Builder()
                .uri("${BASE_URL}/echo")
                .method(HttpMethod.POST)
                .body(requestBody)
                .header("echo-header", "echo")
                .build()

        val response = client.get(request)

        assertEquals(response.headers.get("echo-header"), "echo")

        assertNull(response.headers.get("header-is-not-exists"))

    }

}