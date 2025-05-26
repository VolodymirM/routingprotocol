package com.networks.routingprotocol.main;

import com.networks.routingprotocol.client.Client;
import com.networks.routingprotocol.client.Message;
import com.networks.routingprotocol.router.Router;

public class Main {
    public static void main(String[] args) {
        Router router1 = new Router(8080);
		router1.start();

        Client client1 = new Client(1, 8080);
        client1.sendMessage(new Message(2, "Hello from client 1"));

		Client client2 = new Client(2, 8080);
        client2.sendMessage(new Message(1, "Hello from client 2"));
        
        Router router2 = new Router(8081);
        router2.start();

        router1.connect(8081);
        router2.connect(8080);

        router1.sendToRouter(8081, new Message(2, "Hello from router 1 to router 2"));
    }
}

