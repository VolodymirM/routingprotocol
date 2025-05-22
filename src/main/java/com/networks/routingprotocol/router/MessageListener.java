package com.networks.routingprotocol.router;

import com.networks.routingprotocol.client.Message;

public interface MessageListener {
    void onMessageReceived(Message message, ClientHandler handler);
}
