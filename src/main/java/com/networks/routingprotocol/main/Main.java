package com.networks.routingprotocol.main;

import com.networks.routingprotocol.client.Client;
import com.networks.routingprotocol.client.Message;
import com.networks.routingprotocol.router.Router;

public class Main {
    public static void main(String[] args) {
        Router router1 = new Router(7000);
		router1.start();

        Router router2 = new Router(7001);
        router2.start();

        Router router3 = new Router(7002);
        router3.start();

        Router router4 = new Router(7003);
        router4.start();

        Router router5 = new Router(7004);
        router5.start();
        
        System.out.println("");

        router1.connect(7001);
        router2.connect(7000);
        router1.connect(7002);
        router3.connect(7000);
        router2.connect(7002);
        router3.connect(7001);
        router2.connect(7003);
        router3.connect(7003);
        router4.connect(7001);
        router4.connect(7002);
        router1.connect(7004);
        router5.connect(7000);

        System.out.println("");

        Client client1 = new Client(1, 7000);
        
        @SuppressWarnings("unused")
        Client client2 = new Client(2, 7002);

        @SuppressWarnings("unused")
        Client client3 = new Client(3, 7003);

        System.out.println("");
        
        client1.sendMessage(new Message(3, "Hello from client 1 to client 3"));
    }
}

