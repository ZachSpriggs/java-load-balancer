package com.example.loadbalancer;

import java.util.List;

public class RoundRobinStrategy implements LoadBalancingStrategy {
    private int currentIndex = 0;

    @Override
    public synchronized BackendServer getNextServer(List<BackendServer> servers) {
        if (servers.isEmpty()) {
            throw new IllegalStateException("No healthy servers available");
        }
        BackendServer server = servers.get(currentIndex);
        currentIndex = (currentIndex + 1) % servers.size();
        return server;
    }
}