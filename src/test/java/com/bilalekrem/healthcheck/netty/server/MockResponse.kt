package com.bilalekrem.healthcheck.netty.server

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import java.nio.charset.Charset

abstract class MockResponse(private val status: HttpResponseStatus = HttpResponseStatus.OK,
                            private val headers: HttpHeaders? = null) {

    abstract fun response(request: FullHttpRequest): FullHttpResponse

}

class StringResponse(private val status: HttpResponseStatus,
                     private val _response: String,
                     private val headers: HttpHeaders? = null) : MockResponse(status, headers) {

    override fun response(request: FullHttpRequest): FullHttpResponse {
        val body = _response.toByteArray(charset = Charset.forName("UTF-8"))
        val response = DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(body))

        headers?.let {
            response.headers().add(it)
        }

        return response
    }

}

class LambdaResponse(private val status: HttpResponseStatus,
                     private val lambda: (request: HttpRequest) -> FullHttpResponse,
                     private val headers: HttpHeaders? = null) : MockResponse(status, headers) {

    override fun response(request: FullHttpRequest): FullHttpResponse = lambda(request)

}

class EchoResponse(val status: HttpResponseStatus = HttpResponseStatus.OK) : MockResponse(status) {

    override fun response(request: FullHttpRequest): FullHttpResponse {
        // copy is required, otherwise netty struggle with reference of request.content()
        val response = DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                request.content().copy())


        response.headers().add(request.headers())

        return response
    }

}