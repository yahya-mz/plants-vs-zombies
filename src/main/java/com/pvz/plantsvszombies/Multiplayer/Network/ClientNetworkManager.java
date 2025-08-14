package com.pvz.plantsvszombies.Multiplayer.Network;

import java.io.*;
import java.net.Socket;

import com.pvz.plantsvszombies.Multiplayer.Events.SharedEvent;

/**
 * Client-side network manager that connects to the server
 */
public class ClientNetworkManager extends NetworkManager {
    private Socket serverSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final String serverHost;
    private final int serverPort;
    private String clientId;
    
    public ClientNetworkManager(String serverHost, int serverPort) {
        super();
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }
    
    public ClientNetworkManager(String serverHost) {
        this(serverHost, 12345); // Default port
    }
    
    @Override
    protected void startNetworkThread() {
        networkThread = new Thread(this::clientLoop);
        networkThread.setName("ClientNetworkThread");
        networkThread.start();
    }
    
    @Override
    public void start() {
        try {
            // Connect to server
            serverSocket = new Socket(serverHost, serverPort);
            out = new ObjectOutputStream(serverSocket.getOutputStream());
            in = new ObjectInputStream(serverSocket.getInputStream());
            
            clientId = "Client_" + java.util.UUID.randomUUID().toString().substring(0, 8);
            System.out.println("Connected to server as " + clientId);
            
            super.start();
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    private void clientLoop() {
        // Start receiver thread
        Thread receiverThread = new Thread(this::receiveEvents);
        receiverThread.setName("ClientReceiver");
        receiverThread.start();
        
        // Main sender loop
        while (isRunning() && !serverSocket.isClosed()) {
            try {
                // Process outgoing events - send to server
                SharedEvent event;
                while ((event = getNextOutgoingEvent()) != null) {
                    sendEventToServer(event);
                }
                
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.err.println("Client loop error: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
    
    private void receiveEvents() {
        try {
            while (isRunning() && !serverSocket.isClosed()) {
                try {
                    Object obj = in.readObject();
                    if (obj instanceof SharedEvent) {
                        SharedEvent event = (SharedEvent) obj;
                        notifyIncomingEvent(event);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Error deserializing event from server: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            if (isRunning()) {
                System.err.println("Disconnected from server: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send an event to the server
     */
    private void sendEventToServer(SharedEvent event) {
        try {
            out.writeObject(event);
            out.flush();
        } catch (Exception e) {
            System.err.println("Failed to send event to server: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        super.stop();
        
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        return serverSocket != null && !serverSocket.isClosed() && isRunning();
    }
    
    public String getClientId() {
        return clientId;
    }
}

