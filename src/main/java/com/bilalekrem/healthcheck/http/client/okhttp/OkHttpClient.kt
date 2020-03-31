package com.bilalekrem.healthcheck.http.client.okhttp

import com.bilalekrem.healthcheck.http.HttpMethod
import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.HttpResponse
import com.bilalekrem.healthcheck.http.client.HttpClient
import com.bilalekrem.healthcheck.http.client.exception.HttpMethodNotImplementedException
import com.bilalekrem.healthcheck.util.JSON
import okhttp3.OkHttpClient as ok_OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response


class OkHttpClient: HttpClient {

    private val client: ok_OkHttpClient = ok_OkHttpClient()
    private final val EMPTY_REQUEST_BODY = "".toRequestBody()

    override fun <T : Any> get(request: HttpRequest): HttpResponse<T> = doHttpRequest(request)

    override fun <T : Any> post(request: HttpRequest): HttpResponse<T> = doHttpRequest(request)

    override fun <T : Any> put(request: HttpRequest): HttpResponse<T> = doHttpRequest(request)

    override fun <T : Any> delete(request: HttpRequest): HttpResponse<T> = doHttpRequest(request)

    // -- private functions

    private fun <T : Any>  doHttpRequest(request: HttpRequest): HttpResponse<T> {
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
                    val body = JSON().toJSON(request.body)
                    builder.method(request.method.name ,body.toRequestBody())
                } ?: builder.post(EMPTY_REQUEST_BODY)
            }
            else -> throw HttpMethodNotImplementedException(request.method)
        }

        request.headers.forEach { header -> builder.addHeader(header.name, header.value) }

        return builder.build()
    }

    private fun <T: Any> httpResponse(response: Response): HttpResponse<T> {
        response.body?.let {
            if(it.contentLength() > 0) {
                val obj =  JSON().parseJSON(it.string())
                return HttpResponse(response.code, obj as T)
            }
        }

        return HttpResponse(response.code, null)
    }

}