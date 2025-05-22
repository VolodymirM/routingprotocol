package com.networks.routingprotocol.main;

import com.networks.routingprotocol.client.Client;
import com.networks.routingprotocol.client.Message;
import com.networks.routingprotocol.router.Router;

public class Main {
    public static void main(String[] args) {
        Router router = new Router(1, 8080);
        new Thread(router::start).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
			System.err.println("Error in main thread: " + e.getMessage());
        }

        Client client = new Client(2, 8080);
        client.sendMessage(new Message(2, "Hello from client 2"));
    }
}

