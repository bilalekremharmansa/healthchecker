package com.bilalekrem.healthcheck.http

import com.bilalekrem.healthcheck.http.client.HttpBody
import java.lang.RuntimeException

// similar impl:  https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpRequest.html
class HttpRequest private constructor(
        val method: HttpMethod,
        val uri: String,
        val timeout: Int,
        val headers: HttpHeaders,
        val body: HttpBody?) {

    data class Builder(
            var method: HttpMethod = HttpMethod.GET,
            var uri: String? = null,
            var timeout: Int = 100, // 100 ms
            var headers: HttpHeaders = HttpHeaders(),
            var body: HttpBody? = null) {

        fun method(method: HttpMethod) = apply { this.method = method }
        fun uri(uri: String) = apply { this.uri = uri }
        fun timeout(timeout: Int) = apply { this.timeout = timeout }
        fun header(name: String, value: String) = apply { this.headers.add(name, value) }
        fun body(body: HttpBody) = apply { this.body = body }

        fun build(): HttpRequest {
            if (method == HttpMethod.GET && body != null) {
                throw RequestBodyIsNotPermittedException()
            }

            return uri?.let { HttpRequest(method, it, timeout, headers, body) }
                    ?: throw RuntimeException("Uri is missing")
        }

    }

}

class RequestBodyIsNotPermittedException(): RuntimeException("Body is not permitted for operation")