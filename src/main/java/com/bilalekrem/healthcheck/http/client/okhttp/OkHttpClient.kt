package com.bilalekrem.healthcheck.http.client.okhttp

import com.bilalekrem.healthcheck.http.*
import com.bilalekrem.healthcheck.http.client.HttpBody
import com.bilalekrem.healthcheck.http.client.HttpClient
import com.bilalekrem.healthcheck.http.client.exception.HttpMethodNotImplementedException
import okhttp3.OkHttpClient as ok_OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class OkHttpClient: HttpClient {

    private val client: ok_OkHttpClient = ok_OkHttpClient()
    companion object val EMPTY_REQUEST_BODY = "".toRequestBody()

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
                } ?: builder.post(EMPTY_REQUEST_BODY)
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
        val headers = HttpHeaders().apply {
            response.headers.names().forEach { headerName ->
                this.add(headerName, response.headers[headerName]!!)
            }
        }

        if (response.body != null) {
            val rawBody = response.body!!.bytes()

            // is this check required ?
            if (rawBody.isNotEmpty()) {
                val body = HttpBody.fromByteArray(rawBody)

                return HttpResponse(response.code, body, headers)
            }
        }

        return HttpResponse(response.code, headers)
    }

}