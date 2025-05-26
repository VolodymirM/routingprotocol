package com.networks.routingprotocol.router;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingTable {
    private static RoutingTable instance;
    private final Map<Integer, Integer> clientsPorts = new HashMap<>();
    private final Map<Integer, List<Integer>> routes = new HashMap<>();

    private RoutingTable() {}

    public static synchronized RoutingTable getInstance() {
        if (instance == null) {
            instance = new RoutingTable();
        }
        return instance;
    }

    public synchronized void addRoute(int fromRouter, int toRouter) {
        routes.computeIfAbsent(fromRouter, k -> new ArrayList<>()).add(toRouter);
    }

    public synchronized void addClient(int clientId, int port) {
        clientsPorts.put(clientId, port);
    }

    public synchronized Integer getClientPort(int clientId) {
        return clientsPorts.get(clientId);
    }

    public synchronized List<Integer> getRouterNeighbors(int port) {
        return routes.getOrDefault(port, Collections.emptyList());
    }
}
