package com.example.loadbalancer;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> servers = Arrays.asList("localhost:8081", "localhost:8082");
        LoadBalancer lb = new LoadBalancer(8080, servers);
        lb.start();
    }
}