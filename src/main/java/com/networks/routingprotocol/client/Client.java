package com.networks.routingprotocol.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.networks.routingprotocol.router.RoutingTable;

public class Client {
    private int id;
    private int port;
    private int clientPort;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client(int id, int port) {
        this.id = id;
        this.port = port;

        try {
            this.socket = new Socket("localhost", port);
            this.clientPort = socket.getLocalPort();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("CLIENT\n");
            writer.flush();

            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());

            RoutingTable.getInstance().addClient(id, clientPort);
            RoutingTable.getInstance().addClientConnection(id, port);
            System.out.println("Client " + id + " connected to router on port " + port);
            listeningForMessages();
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
            System.out.println("Sending message from client " + id + ": " + message.getContent());
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    private void listeningForMessages() {
        Thread listenerThread = new Thread(() -> {
            try {
                Object obj;
                while ((obj = in.readObject()) != null) {
                    if (obj instanceof Message message) {
                        System.out.println("Client " + id + " received: " + message.getContent());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client " + id + " error reading message: " + e.getMessage());
            }
        });

        listenerThread.start();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
}
