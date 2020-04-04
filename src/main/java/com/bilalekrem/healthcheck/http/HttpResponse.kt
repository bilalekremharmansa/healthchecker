package com.bilalekrem.healthcheck.http

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.reflect.KClass

data class HttpResponse(
        val statusCode: Int,
        val body: ByteArray = byteArrayOf(),
        val headers: HttpHeaders = HttpHeaders()) {

    constructor(statusCode: Int, body: String): this(statusCode, body.toByteArray())
    constructor(statusCode: Int, body: String, httpHeaders: HttpHeaders):
            this(statusCode, body.toByteArray(), httpHeaders)

    fun <T : Any> body(clazz: KClass<T>): T {
        return jacksonObjectMapper().readValue(body, clazz.java)
    }

    inline fun <reified T> body(): T {
        return jacksonObjectMapper().readValue(body)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HttpResponse

        if (statusCode != other.statusCode) return false
        if (!body.contentEquals(other.body)) return false
        if (headers != other.headers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = statusCode
        result = 31 * result + body.contentHashCode()
        result = 31 * result + headers.hashCode()
        return result
    }

}