package com.networks.routingprotocol.router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.networks.routingprotocol.client.Message;

public class Router  implements MessageListener {
    private int port;
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private final Map<Integer, ClientHandler> routerConnections = new HashMap<>();
    private static final int MAX_CLIENTS = 10;
    
    public Router(int port) {
        this.port = port;
    }
    
    public void startListening() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Router is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                if (clientHandlers.size() >= MAX_CLIENTS) {
                    System.out.println("Max clients reached. Rejecting connection from " + clientSocket.getInetAddress());
                    clientSocket.close();
                    continue;
                }

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clientHandlers.add(clientHandler);
                System.out.println("Client handler added. Total clients: " + clientHandlers.size());

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(Message message, ClientHandler handler) {
        System.out.println("Router received from client " + message.getId() + ": " + message.getContent());

        //TODO: Send message to other (specific) client or router
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != handler) {
                clientHandler.sendMessageToClient(message);
            }
        }
    }

    public void connect(int port) {
        try {
            Socket routerSocket = new Socket("localhost", port);
            System.out.println("Router connected to router at " + port);

            ClientHandler routerHandler = new ClientHandler(routerSocket, this);
            clientHandlers.add(routerHandler);
            routerConnections.put(port, routerHandler);  // <== Add this line

            new Thread(routerHandler).start();
            RoutingTable.getInstance().addRoute(this.port, port);
        } catch (IOException e) {
            System.err.println("Error connecting to another router: " + e.getMessage());
        }
    }


    public void sendToRouter(int port, Message message) {
        ClientHandler handler = routerConnections.get(port);
        if (handler == null) {
            System.out.println("No connection found to router on port: " + port);
            return;
        }

        handler.sendMessageToClient(message);
    }


    public void start() {
        new Thread(this::startListening).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
			System.err.println("Error in main thread: " + e.getMessage());
        }
    }

    public int getPort() {return port;}
    public void setPort(int port) {this.port = port;}
}
