package com.bilalekrem.healthcheck.http

class HttpHeaders {

    // data class HttpHeader(val name: String, val value: String)

    private val headers: MutableMap<String, String> = mutableMapOf()

    fun get(name: String) = headers[name]

    fun add(name: String, value: String) = run { headers[name] = value }

    fun exist(name: String) = name in headers

    fun entries() = headers.entries

}