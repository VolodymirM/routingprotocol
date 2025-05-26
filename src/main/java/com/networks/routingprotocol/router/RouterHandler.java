package com.networks.routingprotocol.router;

import java.net.Socket;

public class RouterHandler extends ClientHandler {

    private int routerPort;

    public RouterHandler(Socket routerSocket, MessageListener listener) {
        super();
        this.clientSocket = routerSocket;
        this.listener = listener;
        this.routerPort = routerSocket.getPort();
    }

    public int getRouterPort() {
        return routerPort;
    }

    public void setRouterPort(int routerPort) {
        this.routerPort = routerPort;
    }
}
