package com.example.loadbalancer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LoadBalancer {
    private final int port;
    private final List<BackendServer> backendServers;
    private final ExecutorService executorService;
    private final LoadBalancingStrategy strategy;
    private final HealthChecker healthChecker;

    public LoadBalancer(int port, List<String> serverAddresses) {
        this.port = port;
        this.backendServers = serverAddresses.stream()
            .map(BackendServer::new)
            .collect(Collectors.toList());
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.strategy = new RoundRobinStrategy(); // Default, need to add more strategies 
        this.healthChecker = new HealthChecker(this.backendServers);
    }

    public void start() {
        healthChecker.startHealthChecks();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Load Balancer is running on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleRequest(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket clientSocket) {
        try {
            BackendServer server = strategy.getNextServer(getHealthyServers());
            forwardRequest(clientSocket, server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forwardRequest(Socket clientSocket, BackendServer server) throws IOException {
        try (
            Socket backendSocket = new Socket(server.getAddress(), server.getPort());
            InputStream clientIn = clientSocket.getInputStream();
            OutputStream clientOut = clientSocket.getOutputStream();
            InputStream backendIn = backendSocket.getInputStream();
            OutputStream backendOut = backendSocket.getOutputStream()
        ) {
            // Forward request to backend server
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = clientIn.read(buffer)) != -1) {
                backendOut.write(buffer, 0, bytesRead);
                if (clientIn.available() <= 0) break;
            }
            backendOut.flush();

            // Forward response to client
            while ((bytesRead = backendIn.read(buffer)) != -1) {
                clientOut.write(buffer, 0, bytesRead);
            }
            clientOut.flush();
        }
    }

    private List<BackendServer> getHealthyServers() {
        return backendServers.stream()
            .filter(BackendServer::isHealthy)
            .collect(Collectors.toList());
    }
}