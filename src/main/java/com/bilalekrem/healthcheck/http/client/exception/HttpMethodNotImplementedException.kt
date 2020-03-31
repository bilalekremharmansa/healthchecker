package com.bilalekrem.healthcheck.http.client.exception

import com.bilalekrem.healthcheck.http.HttpMethod

class HttpMethodNotImplementedException(method: HttpMethod): RuntimeException("Method not implemented: [$method]")