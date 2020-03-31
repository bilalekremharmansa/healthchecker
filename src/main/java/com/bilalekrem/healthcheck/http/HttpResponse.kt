package com.bilalekrem.healthcheck.http

data class HttpResponse<T>(
        val statusCode: Int,
        val body: T?
)