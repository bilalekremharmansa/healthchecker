package com.bilalekrem.healthcheck.yaml

import com.bilalekrem.healthcheck.configuration.yaml.ConfigurationContext
import com.bilalekrem.healthcheck.model.HealthStatus
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.RuntimeException
import java.net.ServerSocket
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestYamlConfiguration {

    val yaml="""definitions:
  - name: tcp-sample
    interval: 500
    checker:
        type: tcp
        ip: 127.0.0.1
        port: 10000
        timeout: 1000
  - name: regex-sample
    interval: 100
    checker:
      type: regex
      request:
        uri: 127.0.0.1
        method: GET
        headers:
          - name: header1
            value: headerValue
          - name: header2
            value: headerValue2
      pattern: myregex
  - name: regex-sample2
    interval: 100
    checker:
      type: regex
      request:
        uri: 127.0.0.1
        method: GET
        headers:
          - name: header1
            value: headerValue
          - name: header2
            value: headerValue2
      pattern: myregex"""

    @Test
    fun `parse yaml`() {
        ConfigurationContext(yaml)
    }

    @Test
    fun `load yaml configuration from file`() {
        ConfigurationContext(File("src/test/resources/healthchecker.yaml"))
    }

    @Test
    fun `fail validation`() {
        val yaml="""definitions:
  - name: same-sample
    interval: 500
    checker:
        type: tcp
        ip: 127.0.0.1
        port: 212
        timeout: 1000
  - name: same-sample
    interval: 500
    checker:
        type: tcp
        ip: 127.0.0.1
        port: 2
        timeout: 2"""

        assertFailsWith(RuntimeException::class) { ConfigurationContext(yaml) }
    }

    @Test
    fun `create tcp health checker and run`() {
        val service = ConfigurationContext(yaml).createService("tcp-sample")

        val port = 10000
        assertEquals(HealthStatus.UNKNOWN, service.status())

        service.start()
        Thread.sleep(300)
        assertEquals(HealthStatus.UNHEALTHY, service.status())

        thread {
            val server = ServerSocket(10000)
            server.accept()
            Thread.sleep(2000)

            // thread is closing..
            server.close()
        }
        // server is running for now..
        Thread.sleep(2000)
        assertEquals(HealthStatus.HEALTHY, service.status())

        // server should be closed for now..
        Thread.sleep(2000)
        assertEquals(HealthStatus.UNHEALTHY, service.status())

        service.stop()
        Thread.sleep(1000)
        // checker is stopped, do not know state of server
        assertEquals(HealthStatus.UNKNOWN, service.status())
    }

}