package com.pvz.plantsvszombies.Presentation.GUI.Views;

import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Multiplayer.Engines.ServerGameEngine;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * UI for multiplayer game setup - server creation and client connection
 */
public class MultiplayerMenuView {
    private Stage primaryStage;
    
    public MultiplayerMenuView() {
    }
    
    public Stage createStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        
        // Background
//        Image backgroundImage = new Image(GlobalSettings.getResource("graphics/Items/Background/daymenu.jpg"));
//        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
//        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
//                                                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
//        root.setBackground(new Background(background));

        // Title
        Label titleLabel = new Label("🌱 Plants vs Zombies - Multiplayer 🧟");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        
        // Server section
        VBox serverSection = createServerSection();
        
        // Client section
        VBox clientSection = createClientSection();
        
        // Separator
        Separator separator = new Separator();
        separator.setMaxWidth(400);
        
        // Back button
        Button backButton = new Button("← Back to Main Menu");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        backButton.setOnAction(e -> {
            primaryStage.show();
            ((Stage) backButton.getScene().getWindow()).close();
        });
        
        root.getChildren().addAll(titleLabel, serverSection, separator, clientSection, backButton);
        
        Scene scene = new Scene(root, 600, 700);
        Stage stage = new Stage();
        stage.setTitle("Multiplayer Setup");
        stage.setScene(scene);
        stage.setResizable(false);
        
        return stage;
    }
    
    private VBox createServerSection() {
        VBox serverBox = new VBox(15);
        serverBox.setAlignment(Pos.CENTER);
        serverBox.setPadding(new Insets(20));
        serverBox.setStyle("-fx-background-color: rgba(76, 175, 80, 0.1); -fx-background-radius: 10;");
        
        Label serverTitle = new Label("🖥️ Host a Game");
        serverTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Game mode selection
        HBox modeBox = new HBox(10);
        modeBox.setAlignment(Pos.CENTER);
        Label modeLabel = new Label("Game Mode:");
        modeLabel.setStyle("-fx-font-size: 14px;");
        
        ToggleGroup modeGroup = new ToggleGroup();
        RadioButton dayMode = new RadioButton("Day Mode");
        RadioButton nightMode = new RadioButton("Night Mode");
        dayMode.setToggleGroup(modeGroup);
        nightMode.setToggleGroup(modeGroup);
        dayMode.setSelected(true);
        
        modeBox.getChildren().addAll(modeLabel, dayMode, nightMode);
        
        // Player count
        HBox playerBox = new HBox(10);
        playerBox.setAlignment(Pos.CENTER);
        Label playerLabel = new Label("Players:");
        playerLabel.setStyle("-fx-font-size: 14px;");
        
        Spinner<Integer> playerSpinner = new Spinner<>(2, 4, 2);
        playerSpinner.setPrefWidth(80);
        
        playerBox.getChildren().addAll(playerLabel, playerSpinner);
        
        // Host participation option
        HBox hostBox = new HBox(10);
        hostBox.setAlignment(Pos.CENTER);
        Label hostLabel = new Label("Host Mode:");
        hostLabel.setStyle("-fx-font-size: 14px;");
        
        ToggleGroup hostGroup = new ToggleGroup();
        RadioButton hostPlay = new RadioButton("Host & Play");
        RadioButton hostOnly = new RadioButton("Host Only");
        hostPlay.setToggleGroup(hostGroup);
        hostOnly.setToggleGroup(hostGroup);
        hostPlay.setSelected(true); // Default to host & play
        
        hostBox.getChildren().addAll(hostLabel, hostPlay, hostOnly);
        
        // Server status
        Label statusLabel = new Label("Ready to host");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        // Start server button
        Button startServerButton = new Button("🚀 Start Server");
        startServerButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 30; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        startServerButton.setOnAction(e -> {
            String gameMode = dayMode.isSelected() ? "day" : "night";
            int playerCount = playerSpinner.getValue();
            boolean hostShouldPlay = hostPlay.isSelected();
            startServer(gameMode, playerCount, hostShouldPlay, statusLabel, startServerButton);
        });
        
        serverBox.getChildren().addAll(serverTitle, modeBox, playerBox, hostBox, statusLabel, startServerButton);
        return serverBox;
    }
    
    private VBox createClientSection() {
        VBox clientBox = new VBox(15);
        clientBox.setAlignment(Pos.CENTER);
        clientBox.setPadding(new Insets(20));
        clientBox.setStyle("-fx-background-color: rgba(33, 150, 243, 0.1); -fx-background-radius: 10;");
        
        Label clientTitle = new Label("🔌 Join a Game");
        clientTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Server address input
        HBox addressBox = new HBox(10);
        addressBox.setAlignment(Pos.CENTER);
        Label addressLabel = new Label("Server Address:");
        addressLabel.setStyle("-fx-font-size: 14px;");
        
        TextField addressField = new TextField("localhost");
        addressField.setPrefWidth(150);
        addressField.setPromptText("Enter server IP");
        
        addressBox.getChildren().addAll(addressLabel, addressField);
        
        // Connection status
        Label connectStatusLabel = new Label("Enter server address to connect");
        connectStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        // Connect button
        Button connectButton = new Button("🔗 Connect to Game");
        connectButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 30; -fx-background-color: #2196F3; -fx-text-fill: white;");
        
        connectButton.setOnAction(e -> {
            String serverAddress = addressField.getText().trim();
            if (serverAddress.isEmpty()) {
                connectStatusLabel.setText("❌ Please enter a server address");
                connectStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
                return;
            }
            connectToServer(serverAddress, connectStatusLabel, connectButton);
        });
        
        clientBox.getChildren().addAll(clientTitle, addressBox, connectStatusLabel, connectButton);
        return clientBox;
    }
    
    private void startServer(String gameMode, int playerCount, boolean hostShouldPlay, Label statusLabel, Button startButton) {
        startButton.setDisable(true);
        statusLabel.setText("🔄 Starting server...");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2196F3;");
        
        // Start server in background thread
        Thread serverThread = new Thread(() -> {
            try {
                ServerGameEngine serverEngine = new ServerGameEngine(
                    GlobalSettings.WIDTH, GlobalSettings.HEIGHT, playerCount);
                
                Platform.runLater(() -> {
                    statusLabel.setText("✅ Server running! Waiting for " + playerCount + " players...");
                    statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green;");
                });
                
                // Start the server
                serverEngine.start();
                
                // Create server monitoring window
                Platform.runLater(() -> createServerMonitorWindow(serverEngine));
                
                // Auto-join the server creator as a player if requested
                if (hostShouldPlay) {
                    Platform.runLater(() -> {
                        try {
                            // Small delay to ensure server is ready
                            Thread.sleep(1000);
                            
                            // Launch the game for the server creator
                            MultiplayerPickingStage pickingStage = new MultiplayerPickingStage("localhost", gameMode);
                            Stage gameStage = pickingStage.createStage(null); // No parent stage
                            gameStage.setTitle("Plants vs Zombies - Host Player");
                            gameStage.show();
                            
                            statusLabel.setText("✅ Server running! You joined as host. Waiting for " + (playerCount - 1) + " more players...");
                            
                        } catch (Exception ex) {
                            statusLabel.setText("⚠️ Server running but failed to auto-join: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    });
                }
                
                // Keep server running
                while (!serverEngine.isGameEnded()) {
                    serverEngine.update();
                    Thread.sleep(33); // ~30 FPS
                }
                
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Failed to start server: " + ex.getMessage());
                    statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
                    startButton.setDisable(false);
                });
                ex.printStackTrace();
            }
        });
        
        serverThread.setName("ServerGameThread");
        serverThread.setDaemon(true);
        serverThread.start();
    }
    
    private void connectToServer(String serverAddress, Label statusLabel, Button connectButton) {
        connectButton.setDisable(true);
        statusLabel.setText("🔄 Connecting to server...");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2196F3;");
        
        // Try to connect in background thread
        Thread connectThread = new Thread(() -> {
            try {
                // Test connection first
                java.net.Socket testSocket = new java.net.Socket();
                testSocket.connect(new java.net.InetSocketAddress(serverAddress, 12345), 5000);
                testSocket.close();
                
                Platform.runLater(() -> {
                    statusLabel.setText("✅ Connected! Starting game...");
                    statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green;");
                    
                    // Launch plant picking stage for client
                    try {
                        MultiplayerPickingStage pickingStage = new MultiplayerPickingStage(serverAddress, "day");
                        Stage gameStage = pickingStage.createStage(primaryStage);
                        gameStage.show();
                        ((Stage) connectButton.getScene().getWindow()).close();
                    } catch (Exception e) {
                        statusLabel.setText("❌ Failed to start game: " + e.getMessage());
                        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
                        connectButton.setDisable(false);
                    }
                });
                
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Connection failed: " + ex.getMessage());
                    statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
                    connectButton.setDisable(false);
                });
            }
        });
        
        connectThread.setName("ConnectionTestThread");
        connectThread.setDaemon(true);
        connectThread.start();
    }
    
    private void createServerMonitorWindow(ServerGameEngine serverEngine) {
        VBox monitorBox = new VBox(10);
        monitorBox.setPadding(new Insets(20));
        monitorBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("🖥️ Server Monitor");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label();
        Label waveLabel = new Label();
        Label playersLabel = new Label();
        
        // Update labels periodically
        Thread updateThread = new Thread(() -> {
            while (!serverEngine.isGameEnded()) {
                Platform.runLater(() -> {
                    statusLabel.setText("Status: " + (serverEngine.isGameStarted() ? "Running" : "Waiting for players"));
                    waveLabel.setText("Current Wave: " + serverEngine.getCurrentWave());
                    playersLabel.setText("Players: " + serverEngine.getAliveClientCount() + "/" + serverEngine.getConnectedClientCount());
                });
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
        
        Button stopButton = new Button("🛑 Stop Server");
        stopButton.setOnAction(e -> {
            serverEngine.stop();
            ((Stage) stopButton.getScene().getWindow()).close();
        });
        
        monitorBox.getChildren().addAll(titleLabel, statusLabel, waveLabel, playersLabel, stopButton);
        
        Scene scene = new Scene(monitorBox, 300, 200);
        Stage monitorStage = new Stage();
        monitorStage.setTitle("Server Monitor");
        monitorStage.setScene(scene);
        monitorStage.show();
    }
}
