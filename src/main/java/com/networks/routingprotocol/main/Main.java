package com.networks.routingprotocol.main;

import com.networks.routingprotocol.client.Client;
import com.networks.routingprotocol.client.Message;
import com.networks.routingprotocol.router.Router;

public class Main {
    public static void main(String[] args) {
        Router router = new Router(1, 8080);
		router.start();

        Client client1 = new Client(1, 8080);
        client1.sendMessage(new Message(2, "Hello from client 1"));

		Client client2 = new Client(2, 8080);
        client2.sendMessage(new Message(1, "Hello from client 2"));
    }
}

