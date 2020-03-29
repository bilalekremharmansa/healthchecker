package com.bilalekrem.healthcheck.http.client

import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.HttpResponse


interface HttpClient<T> {

    fun get(request: HttpRequest): HttpResponse<T>

    fun post(request: HttpRequest): HttpResponse<T>

    fun put(request: HttpRequest): HttpResponse<T>

    fun delete(request: HttpRequest): HttpResponse<T>

}