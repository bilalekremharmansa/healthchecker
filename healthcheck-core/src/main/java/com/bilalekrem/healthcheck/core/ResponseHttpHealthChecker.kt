package com.bilalekrem.healthcheck.core

import com.bilalekrem.healthcheck.http.HttpRequest
import com.bilalekrem.healthcheck.http.HttpResponse
import com.bilalekrem.healthcheck.model.Health
import com.fasterxml.jackson.databind.JsonNode

class ResponseHttpHealthChecker(request: HttpRequest,
                                private val expectedResponse: HttpResponse): HttpHealthChecker(request) {

    override fun check(): Health {
        val response = client.request(request)

        val healthBuilder = Health.builder()
        if (response.statusCode != expectedResponse.statusCode) {
            healthBuilder.error("status code are not exact," +
                    " expected: [${expectedResponse.statusCode}], actual: [${response.statusCode}]")
        }

        for ((header, value) in expectedResponse.headers.entries()) {
            if (response.headers.exist(header)) {
                val responseValue = response.headers.get(header)
                if (responseValue != value) {
                    healthBuilder.error("value is expected for header: [$header]," +
                            " expected: [$value], actual [$responseValue]")
                }
            } else {
                healthBuilder.error("expecting header is not found, expected: [$header]")
            }
        }

        if (expectedResponse.body != null && response.body != null){
            // make sure your response object can be convert to json
            val expectedBody = expectedResponse.body.jsonToObject<JsonNode>()
            val actualBody = response.body.jsonToObject<JsonNode>()

            if (expectedBody != actualBody) {
                healthBuilder.error("response bodies are not matching," +
                        " expected ${expectedBody}, actual: [${actualBody}]")
            }
        } else if (expectedResponse.body == null && response.body == null) {
            // this is ok..
        } else {
            val expectedResponse = expectedResponse.body?.string() ?: "null"
            val actualResponse = response.body?.string() ?: "null"

            healthBuilder.error("response bodies are not matching," +
                    " expected ${expectedResponse}, actual: [${actualResponse}]")
        }

        return healthBuilder.build()
    }

}