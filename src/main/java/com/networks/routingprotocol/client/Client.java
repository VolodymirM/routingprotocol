package com.networks.routingprotocol.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private int id;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    
    public Client(int id, int port) {
        this.id = id;
        this.port = port;
        
        try {
            this.socket = new Socket("localhost", port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Client " + id + " connected to router on port " + port);
        } catch (IOException e) {
            System.out.println("Error creating client socket: " + e.getMessage());
        }
    }

    public void sendMessage(Message message) {
        if (socket == null || socket.isClosed()) {
            System.out.println("Socket is not connected.");
            return;
        }
        
        try {
            System.out.println("Sending...");
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
    
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getPort() {return port;}
    public void setPort(int port) {this.port = port;}
}
