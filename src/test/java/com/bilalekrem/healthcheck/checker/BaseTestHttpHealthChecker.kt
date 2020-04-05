package com.bilalekrem.healthcheck.checker

import com.bilalekrem.healthcheck.netty.server.EchoResponse
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import com.bilalekrem.healthcheck.netty.server.JSONResponse

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpMethod as NettyHttpMethod

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTestHttpHealthChecker {

    private val server = HttpTestServer()

    val PORT = 9999
    val BASE_URL = "http://127.0.0.1:$PORT"

    @BeforeAll
    fun initServer() {
        server.start(PORT)

        server.map(NettyHttpMethod.GET, "/forSuccess", JSONResponse(
                HttpResponseStatus.OK, """{"message": "it is a success" }""")
        )

        server.map(HttpMethod.GET, "/echo", EchoResponse())
        server.map(HttpMethod.POST, "/echo", EchoResponse())
    }

    @AfterAll
    fun closeServer() {
        server.close()
    }

}