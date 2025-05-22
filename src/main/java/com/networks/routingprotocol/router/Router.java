package com.networks.routingprotocol.router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Router {
    private int id;
    private int port;
    private ServerSocket serverSocket;
    
    public Router(int id, int port) {
        this.id = id;
        this.port = port;
    }
    
    public void start() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Router " + id + " is listening on port " + port);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket)).start();
            }
            
        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
        }
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getPort() {return port;}
    public void setPort(int port) {this.port = port;}
}
