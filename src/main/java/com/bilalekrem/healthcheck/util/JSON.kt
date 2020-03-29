package com.bilalekrem.healthcheck.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class JSON {

    val mapper = ObjectMapper()

    fun toJSON(obj: Any): String {
        return mapper.writeValueAsString(obj)
    }

    fun parseJSON(json: String): Any {
        return mapper.readValue(json)
    }


}