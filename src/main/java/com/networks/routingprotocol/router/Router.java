package com.networks.routingprotocol.router;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.networks.routingprotocol.client.Message;

public class Router implements MessageListener {
    private int port;
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private final ArrayList<RouterHandler> routerHandlers = new ArrayList<>();
    private static final int MAX_HANDLERS = 10;

    public Router(int port) {
        this.port = port;
    }

    @SuppressWarnings("ConvertToStringSwitch")
    public void startListening() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Router is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String clientType = reader.readLine(); // Read handshake line (no newline included)

                if (clientType == null) {
                    socket.close();
                    continue;
                }

                if (clientType.equals("CLIENT")) {
                    if (clientHandlers.size() >= MAX_HANDLERS) {
                        System.out.println("Max clients reached. Rejecting connection from " + socket.getInetAddress());
                        socket.close();
                        continue;
                    }

                    ClientHandler clientHandler = new ClientHandler();
                    clientHandler.setClientSocket(socket);
                    clientHandler.setListener(this);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();

                } else if (clientType.equals("ROUTER")) {
                    if (routerHandlers.size() >= MAX_HANDLERS) {
                        System.out.println("Max routers reached. Rejecting connection from " + socket.getInetAddress());
                        socket.close();
                        continue;
                    }

                    RouterHandler routerHandler = new RouterHandler(socket, this);
                    routerHandlers.add(routerHandler);
                    new Thread(routerHandler).start();
                } else {
                    System.out.println("Unknown connection type: " + clientType);
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(Message message, ClientHandler handler) {
        System.out.println("Router received message for client " + message.getId() + ": " + message.getContent());
        
        // TODO: Routing logic goes here.
        // for (ClientHandler clientHandler : clientHandlers) {
        //     if (clientHandler.getClientSocket().getPort() == message.getId()) {
        //         clientHandler.send(message);
        //         System.out.println("Message sent to client " + message.getId() + ": " + message.getContent());
        //         return;
        //     }
        // }

        // for (RouterHandler routerHandler : routerHandlers) {
        //     if (routerHandler.getRouterPort() == message.getId()) {
        //         routerHandler.send(message);
        //         System.out.println("Message sent to router on port " + message.getId() + ": " + message.getContent());
        //         return;
        //     }
        // }
    }

    public void connect(int targetPort) {
        try {
            Socket routerSocket = new Socket("localhost", targetPort);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(routerSocket.getOutputStream()));
            writer.write("ROUTER\n");
            writer.flush();

            RouterHandler routerHandler = new RouterHandler(routerSocket, this);
            routerHandlers.add(routerHandler);
            new Thread(routerHandler).start();
            System.out.println("Router connected to another router on port " + targetPort);
        } catch (IOException e) {
            System.err.println("Error connecting to router on port " + targetPort + ": " + e.getMessage());
        }
    }

    public void sendToRouter(int targetPort, Message message) {
        for (RouterHandler handler : routerHandlers) {
            if (handler.getRouterPort() == targetPort) {
                handler.send(message);
                System.out.println("Message sent to router on port " + targetPort + ": " + message.getContent());
                return;
            }
        }
        System.out.println("No router found on port " + targetPort);
    }

    public void start() {
        new Thread(this::startListening).start();
        try {
            Thread.sleep(100); // Give listener thread a head start
        } catch (InterruptedException e) {
            System.err.println("Error in router thread: " + e.getMessage());
        }
    }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
}
