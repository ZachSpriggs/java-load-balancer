package com.example.loadbalancer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HealthChecker {
    private final List<BackendServer> servers;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public HealthChecker(List<BackendServer> servers) {
        this.servers = servers;
    }

    public void startHealthChecks() {
        scheduler.scheduleAtFixedRate(this::checkServers, 0, 10, TimeUnit.SECONDS);
    }

    private void checkServers() {
        for (BackendServer server : servers) {
            boolean isHealthy = checkServerHealth(server);
            server.setHealthy(isHealthy);
            System.out.println("Server " + server + " health: " + (isHealthy ? "UP" : "DOWN"));
        }
    }

    private boolean checkServerHealth(BackendServer server) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(server.getAddress(), server.getPort()), 3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}