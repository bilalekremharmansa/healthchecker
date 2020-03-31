package com.bilalekrem.healthcheck.http.client

import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.HttpResponse


interface HttpClient {

    fun <T : Any> get(request: HttpRequest): HttpResponse<T>

    fun <T : Any> post(request: HttpRequest): HttpResponse<T>

    fun <T : Any> put(request: HttpRequest): HttpResponse<T>

    fun <T : Any> delete(request: HttpRequest): HttpResponse<T>

}