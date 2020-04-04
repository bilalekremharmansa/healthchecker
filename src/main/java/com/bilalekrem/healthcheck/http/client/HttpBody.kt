package com.bilalekrem.healthcheck.http.client

import com.bilalekrem.healthcheck.util.JSON
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.charset.Charset
import kotlin.reflect.KClass

class HttpBody private constructor(val content: ByteArray) {

    companion object {

        fun fromByteArray(content: ByteArray): HttpBody {
            return HttpBody(content)
        }

        fun objectToJsonBody(obj: Any): HttpBody {
            val json = JSON.toJSON(obj)

            return HttpBody(json.toByteArray())
        }

    }

    fun length() = content.size

    fun isEmpty() = length() == 0

    fun <T : Any> jsonToObject(clazz: KClass<T>): T {
        return jacksonObjectMapper().readValue(content, clazz.java)
    }

    inline fun <reified T> jsonToObject(): T {
        return jacksonObjectMapper().readValue(content)
    }

    fun string(charset: Charset= Charset.defaultCharset()): String {
        return String(content, charset)
    }

}

