package com.bilalekrem.healthcheck.configuration.yaml

import com.bilalekrem.healthcheck.service.HealthServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.logging.log4j.LogManager
import org.reflections.Reflections
import java.io.File

class ConfigurationContext(configuration: String) {

    constructor(configurationFile: File): this(configurationFile.readText())

    private val logger = LogManager.getLogger()

    private val mapper = ObjectMapper(YAMLFactory())

    private val serviceDefinitions = mutableMapOf<String, ServiceDefinitions>()

    init {
        mapper.findAndRegisterModules()
        registerConfigurations()

        try {
            mapper
                .readValue<ConfigurationWrapper>(configuration)
                .definitions
                .forEach { service ->
                    // throw exception if exists, otherwise simply put
                    serviceDefinitions[service.name]
                            ?.let { throw ServiceDefinitionAlreadyDefined(service.name) }
                            ?: serviceDefinitions.put(service.name, service)
                }
        }catch (ex: MissingKotlinParameterException) {
            logger.error("Configuration could not be parsed, missing parameter in configuration: [{}]",
                    ex.parameter.name)
        }
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

    fun createService(name: String): HealthServiceImpl {
        return serviceDefinitions[name]
                ?.let { HealthServiceImpl(it.name, it.checker.createChecker(), it.interval, it.timeout) }
                ?: throw ServiceDefinitionNotFound("Definition name is not exists: [$name]")
    }

    fun createServices(): List<HealthServiceImpl> {
        return serviceDefinitions
                .values
                .map { HealthServiceImpl(it.name, it.checker.createChecker(), it.interval, it.timeout) }
                .toList()
    }

}

class ServiceDefinitionAlreadyDefined(name: String): RuntimeException("Service already defined: [${name}]")
class ServiceDefinitionNotFound(name: String): RuntimeException("Configuration name already defined: [${name}]")
