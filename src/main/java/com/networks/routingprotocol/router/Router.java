package com.networks.routingprotocol.router;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.networks.routingprotocol.client.Message;

public class Router implements MessageListener {
    private int port;
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private final ArrayList<RouterHandler> routerHandlers = new ArrayList<>();
    private static final int MAX_HANDLERS = 10;

    public Router(int port) {
        this.port = port;
    }

    @SuppressWarnings("ConvertToStringSwitch")
    public void startListening() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Router is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String clientType = reader.readLine();

                if (clientType == null) {
                    socket.close();
                    continue;
                }

                if (clientType.equals("CLIENT")) {
                    if (clientHandlers.size() >= MAX_HANDLERS) {
                        System.out.println("Max clients reached. Rejecting connection from " + socket.getInetAddress());
                        socket.close();
                        continue;
                    }

                    ClientHandler clientHandler = new ClientHandler(socket, this);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();

                } else if (clientType.equals("ROUTER")) {
                    if (routerHandlers.size() >= MAX_HANDLERS) {
                        System.out.println("Max routers reached. Rejecting connection from " + socket.getInetAddress());
                        socket.close();
                        continue;
                    }

                    RouterHandler routerHandler = new RouterHandler(socket, this);
                    routerHandlers.add(routerHandler);
                    System.out.println("Router " + socket.getLocalPort() + " connected on port " + routerHandler.getRouterPort());
                    new Thread(routerHandler).start();
                } else {
                    System.out.println("Unknown connection type: " + clientType);
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(Message message, ClientHandler handler) {
        System.out.println("Router: " + port);

        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getClientSocket().getPort() == RoutingTable.getInstance().getClientPort(message.getId())) {
                clientHandler.send(message);
                return;
            }
        }
        
        int nextPort = getNextPortToClient(message.getId());
        if (nextPort != -1) {
            sendToRouter(nextPort, message);
        } else {
            System.out.println("No route found for client " + message.getId());
        }
    }

    public void connect(int targetPort) {
        try {
            Socket routerSocket = new Socket("localhost", targetPort);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(routerSocket.getOutputStream()));
            writer.write("ROUTER\n");
            writer.flush();

            RouterHandler routerHandler = new RouterHandler(routerSocket, this);
            routerHandlers.add(routerHandler);
            RoutingTable.getInstance().addRoute(port, targetPort);
            new Thread(routerHandler).start();
            System.out.println("Router connected to another router on port " + targetPort);
        } catch (IOException e) {
            System.err.println("Error connecting to router on port " + targetPort + ": " + e.getMessage());
        }
    }

    private void sendToRouter(int targetPort, Message message) {
        for (RouterHandler handler : routerHandlers) {
            if (handler.getRouterPort() == targetPort) {
                handler.send(message);
                return;
            }
        }
        System.out.println("No router found on port " + targetPort);
    }

    public void start() {
        new Thread(this::startListening).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("Error in router thread: " + e.getMessage());
        }
    }

    public int getNextPortToClient(int clientId) {
        Integer clientRouterPort = RoutingTable.getInstance().getClientConnection(clientId);
        if (clientRouterPort == null) return -1;

        if (clientRouterPort == port) {
            return -1;
        }

        Set<Integer> visited = new HashSet<>();
        List<Integer> queue = new ArrayList<>();
        List<Integer> firstHops = new ArrayList<>();

        visited.add(port);
        List<Integer> neighbors = RoutingTable.getInstance().getRouterNeighbors(port);
        for (Integer neighbor : neighbors) {
            queue.add(neighbor);
            firstHops.add(neighbor);
        }

        int idx = 0;
        while (idx < queue.size()) {
            int current = queue.get(idx);
            int firstHop = firstHops.get(idx);
            idx++;

            if (current == clientRouterPort) {
                return firstHop;
            }

            if (!visited.contains(current)) {
                visited.add(current);
                for (Integer neighbor : RoutingTable.getInstance().getRouterNeighbors(current)) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        firstHops.add(firstHop);
                    }
                }
            }
        }

        return -1;
    }


    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
}
