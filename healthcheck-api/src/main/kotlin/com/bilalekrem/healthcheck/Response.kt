package com.bilalekrem.healthcheck

data class Response<out T>(
        val data: T,
        val status: Boolean = true
)