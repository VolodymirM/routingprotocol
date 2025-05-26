package com.networks.routingprotocol.router;

import java.net.Socket;

public class RouterHandler extends ClientHandler {

    private int routerPort;

    public RouterHandler(Socket routerSocket, MessageListener listener) {
        super(routerSocket, listener);
        this.routerPort = routerSocket.getPort();
    }

    // @Override
    // public void run() {
    //     try {
    //         out = new ObjectOutputStream(clientSocket.getOutputStream());
    //         out.flush();
    //         in = new ObjectInputStream(clientSocket.getInputStream());

    //         Object inputObj;
    //         while ((inputObj = in.readObject()) != null) {
    //             if (inputObj instanceof Message message) {
    //                 listener.onMessageReceived(message, this);
    //             }
    //         }
    //     } catch (IOException | ClassNotFoundException e) {
    //         System.err.println("Exception in RouterHandler: " + e.getMessage());
    //     } finally {
    //         try {
    //             clientSocket.close();
    //         } catch (IOException e) {
    //             System.err.println("Could not close socket: " + e.getMessage());
    //         }
    //     }
    // }

    public int getRouterPort() {
        return routerPort;
    }

    public void setRouterPort(int routerPort) {
        this.routerPort = routerPort;
    }
}
