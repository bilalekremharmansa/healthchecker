package com.bilalekrem.healthcheck.yaml

import com.bilalekrem.healthcheck.configuration.yaml.ConfigurationContext
import com.bilalekrem.healthcheck.configuration.yaml.Properties

import com.fasterxml.jackson.module.kotlin.readValue

import org.junit.jupiter.api.Test
import java.io.File


class TestYamlConfiguration {

    @Test
    fun test() {
        val ctx = ConfigurationContext()

        val o = ctx.mapper.readValue<Properties>(File("src/test/resources/test.yaml"))

        val a = 5
    }
}