package com.networks.routingprotocol.main;

import com.networks.routingprotocol.client.Client;
import com.networks.routingprotocol.client.Message;
import com.networks.routingprotocol.router.Router;

public class Main {
    public static void main(String[] args) {
        Router router1 = new Router(8080);
		router1.start();

        Router router2 = new Router(8081);
        router2.start();

        Router router3 = new Router(5001);
        router3.start();

        Client client1 = new Client(1, 8080);
        Client client2 = new Client(2, 8080);
        Client client3 = new Client(3, 5001);
        
        client1.sendMessage(new Message(2, "Hello from client 1"));
        client2.sendMessage(new Message(1, "Hello from client 2"));
        client3.sendMessage(new Message(1, "Hello from client 3"));

        router1.connect(8081);
        router2.connect(8080);
        router3.connect(8080);

        router1.sendToRouter(8081, new Message(2, "Hello from router 1 to router 2"));
        router2.sendToRouter(8080, new Message(1, "Hello from router 2 to router 1"));
        router3.sendToRouter(8080, new Message(3, "Hello from router 3 to router 1"));
    }
}

