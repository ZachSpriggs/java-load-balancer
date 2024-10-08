package com.example.loadbalancer;

public class BackendServer {
    private final String address;
    private final int port;
    private boolean isHealthy;

    public BackendServer(String serverAddress) {
        String[] parts = serverAddress.split(":");
        this.address = parts[0];
        this.port = Integer.parseInt(parts[1]);
        this.isHealthy = true;
    }

    public String getAddress() { return address; }
    public int getPort() { return port; }
    public boolean isHealthy() { return isHealthy; }
    public void setHealthy(boolean healthy) { isHealthy = healthy; }

    @Override
    public String toString() {
        return address + ":" + port;
    }
}