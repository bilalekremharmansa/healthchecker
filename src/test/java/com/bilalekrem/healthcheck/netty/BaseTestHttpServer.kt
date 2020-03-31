package com.bilalekrem.healthcheck.netty

import com.bilalekrem.healthcheck.netty.server.HttpTestServer
import com.bilalekrem.healthcheck.netty.server.MockResponse
import io.netty.handler.codec.http.HttpMethod
import org.junit.jupiter.api.AfterAll

open class BaseTestHttpServer {

    companion object {
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

}