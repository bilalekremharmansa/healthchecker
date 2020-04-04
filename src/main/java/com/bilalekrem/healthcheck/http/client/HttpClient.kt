package com.bilalekrem.healthcheck.http.client

import com.bilalekrem.healthcheck.http.HttpMethod
import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.HttpResponse
import com.bilalekrem.healthcheck.http.client.exception.HttpMethodNotImplementedException


interface HttpClient {

    fun get(request: HttpRequest): HttpResponse

    fun post(request: HttpRequest): HttpResponse

    fun put(request: HttpRequest): HttpResponse

    fun delete(request: HttpRequest): HttpResponse

    fun request(request: HttpRequest): HttpResponse {
        return when(request.method) {
            HttpMethod.GET -> get(request)
            HttpMethod.POST -> post(request)
            HttpMethod.PUT -> put(request)
            HttpMethod.DELETE -> delete(request)
            else -> throw HttpMethodNotImplementedException(request.method)
        }
    }

}