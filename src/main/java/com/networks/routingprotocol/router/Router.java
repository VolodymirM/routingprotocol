package com.networks.routingprotocol.router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.networks.routingprotocol.client.Message;

public class Router  implements MessageListener {
    private int port;
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private final ArrayList<RouterHandler> routerHandlers = new ArrayList<>();
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

                for (RouterHandler routerHandler : routerHandlers)
                    if (routerHandler.getRouterPort() == clientSocket.getPort())
                        clientSocket.close();

                if (clientSocket.isClosed()) continue;

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
        // for (ClientHandler clientHandler : clientHandlers) {
        //     if (clientHandler != handler) {
        //         clientHandler.send(message);
        //     }
        // }
    }

    public void connect(int port) {
        try {
            Socket routerSocket = new Socket("localhost", port);
            RouterHandler routerHandler = new RouterHandler(routerSocket, this);
            routerHandlers.add(routerHandler);
            System.out.println("Router connected to another router on port " + port);
            new Thread(routerHandler).start();
        } catch (IOException e) {
            System.err.println("Error connecting to router: " + e.getMessage());
        }
    }

    public void sendToRouter(int port, Message message) {
        for (RouterHandler routerHandler : routerHandlers) {
            if (routerHandler.getRouterPort() == port) {
                routerHandler.send(message);
                System.out.println("Message sent to router on port " + port + ": " + message.getContent());
                return;
            }
        }
        System.out.println("No router found on port " + port);
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
