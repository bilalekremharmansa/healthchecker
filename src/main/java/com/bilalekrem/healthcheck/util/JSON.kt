package com.bilalekrem.healthcheck.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object JSON {

    val mapper = jacksonObjectMapper()

    fun toJSONNode(str: String): JsonNode {
        return mapper.readTree(str)
    }

    fun toJSON(obj: Any): String {
        return mapper.writeValueAsString(obj)
    }

    fun parseJSON(json: String): Any {
        return mapper.readValue(json)
    }

}