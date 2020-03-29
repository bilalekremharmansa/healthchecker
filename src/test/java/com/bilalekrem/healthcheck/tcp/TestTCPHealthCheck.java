package com.bilalekrem.healthcheck.tcp;

import com.bilalekrem.healthcheck.core.HealthChecker;
import com.bilalekrem.healthcheck.core.TCPHealthChecker;
import com.bilalekrem.healthcheck.model.Health;
import com.bilalekrem.healthcheck.model.HealthStatus;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestTCPHealthCheck {

    private static ServerSocket serverSocket;

    @BeforeAll
    static void initializeServerThread() throws IOException {
        serverSocket = new ServerSocket(0);
    }

    @AfterAll
    static void closeServer() throws IOException {
        if (! serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    @Test
    void testTcpHealthCheckHealthy() {
        String host = serverSocket.getInetAddress().getHostName();
        int port = serverSocket.getLocalPort();

        HealthChecker healthChecker = new TCPHealthChecker(host, port);
        Health health = healthChecker.check();

        assertEquals(health.getStatus(), HealthStatus.HEALTHY);
    }

    @Test
    void testTcpHealthCheckUnhealthy() {
        String host = serverSocket.getInetAddress().getHostName();

        // choose wrong port
        int port = serverSocket.getLocalPort() + 1;

        HealthChecker healthChecker = new TCPHealthChecker(host, port);
        Health health = healthChecker.check();

        assertEquals(health.getStatus(), HealthStatus.UNHEALTHY);
    }

}
