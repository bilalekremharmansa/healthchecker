package com.bilalekrem.healthcheck.checker;

import com.bilalekrem.healthcheck.core.HealthChecker;
import com.bilalekrem.healthcheck.core.TCPHealthChecker;
import com.bilalekrem.healthcheck.model.Health;
import com.bilalekrem.healthcheck.model.HealthStatus;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestTCPHealthCheck {

    @Test
    void testTcpHealthCheckHealthy() throws IOException {
        ServerSocket serverSocket = createServerSocket();

        String host = serverSocket.getInetAddress().getHostName();
        int port = serverSocket.getLocalPort();

        HealthChecker healthChecker = new TCPHealthChecker(host, port);
        Health health = healthChecker.check();

        assertEquals(health.getStatus(), HealthStatus.HEALTHY);
    }

    @Test
    void testTcpHealthCheckWithTimeoutUnhealthy() throws IOException {

        // -- accept all waiting connections in server socket, set maximum queue length for incoming connections as "1"

        ServerSocket serverSocket = createServerSocket(0, 1);

        // fill the queue for incoming connections

        String host = serverSocket.getInetAddress().getHostName();
        int port = serverSocket.getLocalPort();

        // no one will be accept the socket below, so the queue is full for now
        Socket socket = new Socket(host, port);

        // -- start a thread to accept the socket above for 5 seconds later

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Socket incomingRequest = serverSocket.accept();

                // now queue is released
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        HealthChecker healthChecker = new TCPHealthChecker(host, port, 10000);
        Health health = healthChecker.check();

        assertEquals(health.getStatus(), HealthStatus.HEALTHY);
    }

    @Test
    void testTcpHealthCheckWithTimeoutHealthy() throws IOException {

        // -- accept all waiting connections in server socket, set maximum queue length for incoming connections as "1"

        ServerSocket serverSocket = createServerSocket(0, 1);

        // fill the queue length for incoming connections

        String host = serverSocket.getInetAddress().getHostName();
        int port = serverSocket.getLocalPort();

        // no one will be accept the socket below, so the queue is full for now
        Socket socket = new Socket(host, port);

        // -- start a thread to accept the socket above for 5 seconds later

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Socket incomingRequest = serverSocket.accept();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        HealthChecker healthChecker = new TCPHealthChecker(host, port, 3000);
        Health health = healthChecker.check();

        assertEquals(health.getStatus(), HealthStatus.UNHEALTHY);
    }

    @Test
    void testTcpHealthCheckUnhealthy() throws IOException {

        String host = "127.0.0.1";
        int port = 56502;

        HealthChecker healthChecker = new TCPHealthChecker(host, port);
        Health health = healthChecker.check();

        assertEquals(health.getStatus(), HealthStatus.UNHEALTHY);
    }

    // --------- utils

    private ServerSocket createServerSocket() throws IOException {
        return new ServerSocket(0);
    }

    private ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    private ServerSocket createServerSocket(int port, int backlog) throws IOException {
        return new ServerSocket(port, backlog);
    }

}
