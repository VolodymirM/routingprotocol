package com.networks.routingprotocol.router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.networks.routingprotocol.client.Message;

public class Router  implements MessageListener {
    private int id;
    private int port;
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static final int MAX_CLIENTS = 10;
    
    public Router(int id, int port) {
        this.id = id;
        this.port = port;
    }
    
    public void startListening() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Router " + id + " is listening on port " + port);

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

        //TODO: Send message to other client or router
    }

    public void start() {
        new Thread(this::startListening).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
			System.err.println("Error in main thread: " + e.getMessage());
        }
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getPort() {return port;}
    public void setPort(int port) {this.port = port;}
}
