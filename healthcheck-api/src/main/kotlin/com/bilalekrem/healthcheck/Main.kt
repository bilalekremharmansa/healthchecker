package com.bilalekrem.healthcheck

import com.bilalekrem.healthcheck.configuration.HealthCheckerConfiguration
import com.bilalekrem.healthcheck.configuration.HealthCheckerProperties
import com.bilalekrem.healthcheck.configuration.ServiceDefinitions
import com.fasterxml.jackson.databind.jsontype.NamedType
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.reflections.Reflections
import org.slf4j.LoggerFactory

class Main
fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger(Main::class.java)

    val port = System.getProperty("server.port", "8080").toInt();
    logger.info("Server is listening from [:$port]")

    val server = embeddedServer(Netty, port = port) {

        install(ContentNegotiation) {
            registerJackson()
        }

        install(StatusPages) {
            exception<Throwable> { cause ->
                val response = "${cause.message ?: "${HttpStatusCode.BadRequest}"}\n"
                call.respond(response)
            }
        }

        // required lock access for health controller operations??
        val controller = HealthController()

        routing {
            post("/create") {
                val a = call.receive<ServiceDefinitions>()
                val startOnCreate: Boolean = call.request.queryParameters["start"]?.let { it == "false" } ?: true

                controller.create(a, startOnCreate)

                call.respond(Response("created"))
            }

            delete("/destroy/{name}") {
                val name = call.parameters["name"]!!
                controller.destroy(name)

                call.respond(Response("destroyed"))
            }

            put("/start/{name}") {
                val name = call.parameters["name"]!!
                controller.start(name)

                call.respond(Response("started"))
            }

            put("/stop/{name}") {
                val name = call.parameters["name"]!!
                controller.stop(name)

                call.respond(Response("stopped"))
            }

            get("/status/{name}") {
                val name = call.parameters["name"]!!
                val status = controller.status(name)

                call.respond(Response(status))
            }

        }
    }
    server.start(wait = true)
}

fun ContentNegotiation.Configuration.registerJackson() {
    jackson {
        findAndRegisterModules()
        Reflections()
                .getSubTypesOf(HealthCheckerProperties::class.java)
                .forEach {
                    val annotation = it.getAnnotation(HealthCheckerConfiguration::class.java)

                    // if has annotation register with annotation name, otherwise use class name
                    val registrationName = annotation?.typeName ?: it.simpleName
                    this.registerSubtypes(NamedType(it, registrationName));
                }
    }

}