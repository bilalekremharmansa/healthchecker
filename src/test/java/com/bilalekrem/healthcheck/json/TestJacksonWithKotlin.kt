package com.bilalekrem.healthcheck.json


import com.bilalekrem.healthcheck.util.JSON
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestJacksonWithKotlin {

    private val logger = LogManager.getLogger()

    data class Person(val age: Int, val name: String = "42")

    @Test
    fun testJsonSerialization() {

        val person = Person(1, name="health checker")

        val json = JSON().toJSON(person)

        logger.info("json output of person object:\n${json}")

        assertEquals("{\"age\":1,\"name\":\"health checker\"}", json)
    }


}