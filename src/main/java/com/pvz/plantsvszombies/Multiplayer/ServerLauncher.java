package com.pvz.plantsvszombies.Multiplayer;

import com.pvz.plantsvszombies.Domain.Common.GameMode;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Multiplayer.Engines.ServerGameEngine;

/**
 * Standalone server launcher for dedicated server hosting
 */
public class ServerLauncher {
    
    public static void main(String[] args) {
        System.out.println("🌱 Plants vs Zombies - Multiplayer Server 🧟");
        System.out.println("==========================================");
        
        // Parse command line arguments
        int playerCount = 2;
        GameMode gameMode = GameMode.DAY;
        
        if (args.length >= 1) {
            try {
                playerCount = Integer.parseInt(args[0]);
                if (playerCount < 2 || playerCount > 4) {
                    System.err.println("Player count must be between 2 and 4. Using default: 2");
                    playerCount = 2;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid player count. Using default: 2");
            }
        }
        
        System.out.println("Server Configuration:");
        System.out.println("- Max Players: " + playerCount);
        System.out.println("- Game Mode: " + gameMode.name());
        System.out.println("- Port: 12345");
        System.out.println();
        
        try {
            // Create and start server
            ServerGameEngine serverEngine = new ServerGameEngine(
                GlobalSettings.WIDTH, GlobalSettings.HEIGHT, playerCount, GameMode.DAY);
            
            System.out.println("🚀 Starting server...");
            serverEngine.start();
            
            // Main server loop
            System.out.println("✅ Server is running! Waiting for players to connect...");
            System.out.println("📝 Server status will be displayed below:");
            System.out.println();
            
            long lastStatusUpdate = 0;
            while (!serverEngine.isGameEnded()) {
                serverEngine.update();
                
                // Print status updates every 5 seconds
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastStatusUpdate > 5000) {
                    printServerStatus(serverEngine);
                    lastStatusUpdate = currentTime;
                }
                
                Thread.sleep(33); // ~30 FPS
            }
            
            System.out.println("\n🏁 Game ended! Server shutting down...");
            serverEngine.stop();
            
        } catch (Exception e) {
            System.err.println("❌ Server error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void printServerStatus(ServerGameEngine server) {
        String status = server.isGameStarted() ? 
            String.format("🎮 Playing (Wave %d)", server.getCurrentWave()) : 
            "⏳ Waiting for players";
        
        System.out.printf("[%s] %s | Players: %d/%d alive, %d/%d total%n",
            getCurrentTime(),
            status,
            server.getAliveClientCount(),
            server.getConnectedClientCount(),
            server.getConnectedClientCount(),
            4 // Max players (could be made configurable)
        );
    }
    
    private static String getCurrentTime() {
        return java.time.LocalTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

