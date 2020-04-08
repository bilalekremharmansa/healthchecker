package com.bilalekrem.healthcheck.core;

import com.bilalekrem.healthcheck.model.Health;
import com.bilalekrem.healthcheck.model.HealthStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPHealthChecker implements HealthChecker {

    private static Logger logger = LogManager.getLogger();

    private static final int DEFAULT_SO_TIMEOUT = 100;

    private String host;
    private int port;
    private int timeout = DEFAULT_SO_TIMEOUT;

    public TCPHealthChecker(String ip, int port) {
        this.host = ip;
        this.port = port;
    }

    public TCPHealthChecker(String ip, int port, int timeout) {
        this.host = ip;
        this.port = port;
        this.timeout = timeout;
    }

    public Health check() {
        HealthStatus healthStatus;
        try(Socket socket = new Socket()) {
            InetSocketAddress endpoint = new InetSocketAddress(host, port);
            socket.connect(endpoint, timeout);

            healthStatus = HealthStatus.HEALTHY;
        } catch (IOException e) {
            logger.atTrace().withThrowable(e.getCause()).log("An exception occured while checking {}:{}", host, port);

            healthStatus = HealthStatus.UNHEALTHY;
        }

        return Health.builder().status(healthStatus).build();
    }

}
