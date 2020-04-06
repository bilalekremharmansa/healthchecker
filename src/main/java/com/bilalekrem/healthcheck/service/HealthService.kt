package com.bilalekrem.healthcheck.service

import com.bilalekrem.healthcheck.model.Health
import com.bilalekrem.healthcheck.model.HealthStatus

interface HealthService {

    /*
    * Starts service to check health of current service
    * */
    fun start()

    /*
    * stop checking health of current service
    * */
    fun stop()

    /*
    * stop checking health of current service
    * */
    fun health(): Health

    /*
    * stop checking health of current service
    * */
    fun status(): HealthStatus = health().status

}