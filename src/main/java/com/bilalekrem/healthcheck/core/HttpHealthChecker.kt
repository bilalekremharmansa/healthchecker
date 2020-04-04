package com.bilalekrem.healthcheck.core

import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.client.HttpClient

import com.bilalekrem.healthcheck.http.client.okhttp.OkHttpClient

abstract class HttpHealthChecker(protected val request: HttpRequest,
                                 protected val client: HttpClient = OkHttpClient()): HealthChecker