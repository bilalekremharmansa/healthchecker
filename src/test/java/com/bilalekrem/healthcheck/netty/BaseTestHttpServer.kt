package com.bilalekrem.healthcheck.netty

import com.bilalekrem.healthcheck.netty.server.HttpServerContext
import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import io.netty.handler.codec.http.HttpMethod
import org.junit.jupiter.api.AfterAll

open class BaseTestHttpServer {

    companion object {
        private val server = HttpTestServer()

        fun start(port: Int) = Thread(Runnable { server.start(port) }).start()

        fun map(method: HttpMethod, endpoint: String, response: HttpServerContext.MockResponse)
                = server.map(method, endpoint, response)

        fun close() = server.close()

        @AfterAll
        @JvmStatic
        fun closeServer() {
            close()
        }
    }

}