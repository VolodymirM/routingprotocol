package com.networks.routingprotocol.router;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.networks.routingprotocol.client.Message;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final MessageListener listener;

    public ClientHandler(Socket clientSocket, MessageListener listener) {
        this.clientSocket = clientSocket;
        this.listener = listener;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            Object inputObj;
            while ((inputObj = in.readObject()) != null) {
                if (inputObj instanceof Message message) {
                    // Notify the Router
                    listener.onMessageReceived(message, this);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Exception in ClientHandler: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close socket: " + e.getMessage());
            }
        }
    }
}
