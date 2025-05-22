package com.networks.routingprotocol.router;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.networks.routingprotocol.client.Message;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            Object inputObj;
            while ((inputObj = in.readObject()) != null) {
                if (!(inputObj instanceof Message)) {
                    continue;
                }
                Message inputMessage = (Message) inputObj;

                System.out.println("Received from "+ inputMessage.getId() + ": " + inputMessage.getContent());
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