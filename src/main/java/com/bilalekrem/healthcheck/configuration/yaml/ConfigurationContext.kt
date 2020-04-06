package com.bilalekrem.healthcheck.configuration.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.reflections.Reflections


class ConfigurationContext {

    val mapper = ObjectMapper(YAMLFactory())

    init {
        mapper.findAndRegisterModules()
        registerConfigurations()
    }

    private fun registerConfigurations() {
        Reflections()
                .getSubTypesOf(HealthCheckerProperties::class.java)
                .forEach {
                    val annotation = it.getAnnotation(HealthCheckerConfiguration::class.java)

                    // if has annotation register with annotation name, otherwise use class name
                    val registrationName = annotation?.typeName ?: it.simpleName
                    mapper.registerSubtypes(NamedType(it, registrationName));
                }
    }

}