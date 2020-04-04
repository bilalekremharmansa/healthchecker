package com.bilalekrem.healthcheck.http

import com.bilalekrem.healthcheck.http.client.HttpBody

class HttpResponse(
        val statusCode: Int,
        val body: HttpBody?,
        val headers: HttpHeaders) {

    constructor(statusCode: Int): this(statusCode, null, HttpHeaders())
    constructor(statusCode: Int, body: HttpBody): this(statusCode, body, HttpHeaders())
    constructor(statusCode: Int, headers: HttpHeaders): this(statusCode, null, headers)

}