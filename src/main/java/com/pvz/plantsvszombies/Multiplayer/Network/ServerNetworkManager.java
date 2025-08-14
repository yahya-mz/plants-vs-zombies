package com.pvz.plantsvszombies.Multiplayer.Network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import com.pvz.plantsvszombies.Multiplayer.Events.SharedEvent;

/**
 * Server-side network manager that handles multiple client connections
 */
public class ServerNetworkManager extends NetworkManager {
    private static final int DEFAULT_PORT = 12345;
    private ServerSocket serverSocket;
    private final CopyOnWriteArrayList<ClientConnection> clientConnections;
    private final int maxClients;
    private volatile boolean acceptingConnections = true; // Add this flag
    
    public ServerNetworkManager() {
        this(DEFAULT_PORT, 4); // Default max 4 clients
    }
    
    public ServerNetworkManager(int port, int maxClients) {
        super();
        this.maxClients = maxClients;
        this.clientConnections = new CopyOnWriteArrayList<>();
        
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void startNetworkThread() {
        networkThread = new Thread(this::serverLoop);
        networkThread.setName("ServerNetworkThread");
        networkThread.start();
        
        // Start client acceptance thread
        Thread acceptThread = new Thread(this::acceptClients);
        acceptThread.setName("ClientAcceptThread");
        acceptThread.start();
    }
    
    private void acceptClients() {
        while (isRunning() && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                if (!acceptingConnections) {
                    // Game already started
                    try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
                        out.writeObject("ERROR: Game already in progress");
                    }
                    clientSocket.close();
                    System.out.println("Client rejected - game in progress");
                    continue;
                }
                
                if (clientConnections.size() < maxClients) {
                    String clientId = "Client_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                    ClientConnection connection = new ClientConnection(clientId, clientSocket);
                    clientConnections.add(connection);
                    
                    // Start thread for this client
                    Thread clientThread = new Thread(() -> handleClient(connection));
                    clientThread.setName("ClientHandler-" + clientId);
                    clientThread.start();
                    
                    System.out.println("Client connected: " + clientId + " (" + clientConnections.size() + "/" + maxClients + ")");
                } else {
                    // Server full
                    try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
                        out.writeObject("ERROR: Server full");
                    }
                    clientSocket.close();
                    System.out.println("Client rejected - server full");
                }
            } catch (IOException e) {
                if (isRunning()) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        }
    }
    
    private void handleClient(ClientConnection connection) {
        try {
            ObjectInputStream in = new ObjectInputStream(connection.socket.getInputStream());
            
            while (isRunning() && !connection.socket.isClosed()) {
                try {
                    Object obj = in.readObject();
                    if (obj instanceof SharedEvent) {
                        SharedEvent event = (SharedEvent) obj;
                        notifyIncomingEvent(event);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Error deserializing event from " + connection.clientId + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + connection.clientId);
        } finally {
            removeClient(connection);
        }
    }
    
    private void serverLoop() {
        while (isRunning()) {
            try {
                // Process outgoing events - broadcast to all clients
                SharedEvent event;
                while ((event = getNextOutgoingEvent()) != null) {
                    broadcastEvent(event);
                }
                
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.err.println("Server loop error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Broadcast an event to all connected clients
     */
    public void broadcastEvent(SharedEvent event) {
        clientConnections.removeIf(connection -> {
            try {
                connection.out.writeObject(event);
                connection.out.flush();
                return false; // Keep connection
            } catch (Exception e) {
                System.err.println("Failed to send to client " + connection.clientId + ": " + e.getMessage());
                try {
                    connection.socket.close();
                } catch (IOException ioException) {
                    // Ignore
                }
                return true; // Remove connection
            }
        });
    }
    
    private void removeClient(ClientConnection connection) {
        clientConnections.remove(connection);
        try {
            connection.socket.close();
        } catch (IOException e) {
            // Ignore
        }
        System.out.println("Client removed: " + connection.clientId + " (" + clientConnections.size() + "/" + maxClients + ")");
    }
    
    @Override
    public void stop() {
        super.stop();
        
        // Close all client connections
        for (ClientConnection connection : clientConnections) {
            try {
                connection.socket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        clientConnections.clear();
        
        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }
    
    public int getConnectedClientCount() {
        return clientConnections.size();
    }
    
    public int getMaxClients() {
        return maxClients;
    }
    
    /**
     * Stop accepting new client connections
     */
    public void stopAcceptingConnections() {
        acceptingConnections = false;
        System.out.println("Server no longer accepting new connections");
    }
    
    /**
     * Inner class to represent a client connection
     */
    private static class ClientConnection {
        final String clientId;
        final Socket socket;
        final ObjectOutputStream out;
        
        ClientConnection(String clientId, Socket socket) throws IOException {
            this.clientId = clientId;
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
        }
    }
}
