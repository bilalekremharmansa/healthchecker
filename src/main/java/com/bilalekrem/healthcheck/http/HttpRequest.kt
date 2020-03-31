package com.bilalekrem.healthcheck.http

import java.lang.RuntimeException

// similar impl:  https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpRequest.html
class HttpRequest private constructor(
        val method: HttpMethod,
        val uri: String,
        val timeout: Int,
        val headers: List<HttpHeader>,
        val body: Any? = null
) {

    data class Builder(
            var method: HttpMethod = HttpMethod.GET,
            var uri: String? = null,
            var timeout: Int = 100, // 100 ms
            var headers: List<HttpHeader> = listOf(),
            var body: Any? = null) {

        fun method(method: HttpMethod) = apply { this.method = method }
        fun uri(uri: String) = apply { this.uri = uri }
        fun timeout(timeout: Int) = apply { this.timeout = timeout }
        fun headers(headers: List<HttpHeader>) = apply { this.headers = headers }
        fun body(body: Any) = apply { this.body = body }

        fun build() =
                uri?.let { HttpRequest(method, it, timeout, headers, body) }
                ?: throw RuntimeException("Uri is missing")
    }

}