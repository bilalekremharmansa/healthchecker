package com.bilalekrem.healthcheck.http.client.okhttp

import com.bilalekrem.healthcheck.http.*
import com.bilalekrem.healthcheck.http.client.HttpBody
import com.bilalekrem.healthcheck.http.client.HttpClient
import com.bilalekrem.healthcheck.http.client.exception.HttpMethodNotImplementedException
import okhttp3.OkHttpClient as ok_OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.apache.logging.log4j.LogManager

class OkHttpClient: HttpClient {

    private val logger = LogManager.getLogger()

    private val client: ok_OkHttpClient = ok_OkHttpClient()

    override fun get(request: HttpRequest): HttpResponse = doHttpRequest(request)

    override fun post(request: HttpRequest): HttpResponse = doHttpRequest(request)

    override fun put(request: HttpRequest): HttpResponse = doHttpRequest(request)

    override fun delete(request: HttpRequest): HttpResponse = doHttpRequest(request)

    // -- private functions

    private fun doHttpRequest(request: HttpRequest): HttpResponse {
        val okHttpRequest: Request = buildOkHttpRequestObject(request)

        client.newCall(okHttpRequest).execute().use { response -> return httpResponse(response) }
    }

    private fun buildOkHttpRequestObject(request: HttpRequest): Request {
        val builder = Request.Builder()

        builder.url(request.uri)

        when(request.method) {
            HttpMethod.GET -> builder.get()
            HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE -> {
                request.body?.let {
                    builder.method(request.method.name, it.content.toRequestBody())
                } ?: logger.warn("empty body!")
            }
            else -> throw HttpMethodNotImplementedException(request.method)
        }

        for((name, value) in request.headers.entries() ) {
            builder.addHeader(name, value)
        }

        return builder.build()
    }

    private fun httpResponse(response: Response): HttpResponse {
        // map ( headerName -> headerName, headerValue )
        logger.trace("OkHttp response is received...")
        val headers = HttpHeaders().apply {
            response.headers.names().forEach { headerName ->
                val headerValue = response.headers[headerName]!!
                logger.trace("headers: $headerName -> $headerValue")
                this.add(headerName, headerValue)
            }
        }

        if (response.body != null) {
            val rawBody = response.body!!.bytes()

            // is this check required ?
            if (rawBody.isNotEmpty()) {
                val body = HttpBody.fromByteArray(rawBody)
                logger.trace("response has body: ${body.string()}")

                return HttpResponse(response.code, body, headers)
            }
        }

        return HttpResponse(response.code, headers)
    }

}