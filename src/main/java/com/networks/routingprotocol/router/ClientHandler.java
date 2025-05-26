package com.networks.routingprotocol.router;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.networks.routingprotocol.client.Message;

public class ClientHandler implements Runnable {
    protected Socket clientSocket = new Socket();
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
    protected MessageListener listener = null;
    
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            Object inputObj;
            while ((inputObj = in.readObject()) != null) {
                if (inputObj instanceof Message message) {
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
    
    public void send(Message message) {
         try {
            if (out != null) {
                out.reset();
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Failed to send message to client: " + e.getMessage());
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public MessageListener getListener() {
        return listener;
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }
}
