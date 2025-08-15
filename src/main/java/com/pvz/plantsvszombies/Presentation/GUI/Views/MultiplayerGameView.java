package com.pvz.plantsvszombies.Presentation.GUI.Views;

import java.util.ArrayList;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import com.pvz.plantsvszombies.Multiplayer.Engines.ClientGameEngine;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main game view for multiplayer mode
 */
public class MultiplayerGameView {
    
    public static final int Width = GlobalSettings.WIDTH;
    public static final int Height = GlobalSettings.HEIGHT;
    
    private ClientGameEngine clientEngine;
    private String serverAddress;
    private String gameMode;
    private ArrayList<AbstractPlantGameObject.PlantType> selectedPlants;
    
    // UI Elements for multiplayer info
    private Label connectionStatusLabel;
    private Label gameStatusLabel;
    private Label waveLabel;
    private Label playerStatusLabel;
    
    // Game components
    protected Pane gameBoxPane;
    protected boolean isShovelMode = false;
    
    // Visual engine reference
    private Object visualEngine;
    
    private MultiplayerGameView(ArrayList<AbstractPlantGameObject.PlantType> selectedPlants, 
                              String serverAddress, String gameMode) {
        this.selectedPlants = selectedPlants;
        this.serverAddress = serverAddress;
        this.gameMode = gameMode;
    }
    
    public static Stage createStage(ArrayList<AbstractPlantGameObject.PlantType> selectedPlants, 
                                   String serverAddress, String gameMode) {
        MultiplayerGameView view = new MultiplayerGameView(selectedPlants, serverAddress, gameMode);
        Stage stage = new Stage();
        view.setupStage(stage);
        return stage;
    }
    
    /**
     * Create stage with an already-connected ClientGameEngine
     */
    public static Stage createStage(ClientGameEngine connectedClientEngine, 
                                   ArrayList<AbstractPlantGameObject.PlantType> selectedPlants, 
                                   String serverAddress, String gameMode) {
        MultiplayerGameView view = new MultiplayerGameView(selectedPlants, serverAddress, gameMode);
        view.clientEngine = connectedClientEngine; // Use the already-connected engine
        Stage stage = new Stage();
        view.setupStage(stage);
        return stage;
    }
    
    private void setupStage(Stage stage) {
        try {
            // Don't create a new client engine - use the one passed in
            if (clientEngine == null) {
                throw new IllegalStateException("ClientGameEngine must be provided and connected before creating MultiplayerGameView");
            }
            
            // Start the client engine if it hasn't been started yet
            if (!clientEngine.isConnected()) {
                System.out.println("DEBUG: Starting client engine...");
                clientEngine.start();
                
                // Wait a moment for connection to establish
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Ignore interruption
                }
                
                System.out.println("DEBUG: After start, isConnected: " + clientEngine.isConnected());
                
                // Send ready status to server with selected plants
                if (clientEngine.isConnected()) {
                    System.out.println("DEBUG: Sending ready status...");
                    clientEngine.sendReadyStatus(selectedPlants);
                    System.out.println("Sent ready status to server with plants: " + selectedPlants);
                } else {
                    System.out.println("DEBUG: Client not connected, cannot send ready status");
                }
            } else {
                System.out.println("DEBUG: Client already connected, sending ready status directly");
                clientEngine.sendReadyStatus(selectedPlants);
                System.out.println("Sent ready status to server with plants: " + selectedPlants);
            }
            
            // Initialize visual engine based on game mode (simplified for now)
            // TODO: Properly integrate with visual engines
            
            // Initialize mediator
            Mediator.init(clientEngine, null);
            
            // Setup UI
            setupUI(stage);
            
            // Don't start game engines here - the client engine is already running
            // Mediator.getInstance().startGameEngine();
            
            // Setup close handler
            stage.setOnCloseRequest(e -> {
                if (clientEngine != null) {
                    clientEngine.disconnect();
                }
                Mediator.getInstance().stopGameEngine();
            });
            
        } catch (Exception e) {
            System.err.println("Failed to setup multiplayer game: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private void setupUI(Stage stage) {
        // Create main layout
        BorderPane root = new BorderPane();
        
        // Game area (center)
        gameBoxPane = new Pane();
        gameBoxPane.setPrefSize(Width, Height);
        
        // Background
        String backgroundPath = gameMode.equals("night") ? 
            "graphics/Items/Background/daymap.jpg" :
            "graphics/Items/Background/nightmap.jpg";
        
        Image backgroundImage = new Image(GlobalSettings.getResource(backgroundPath));
        BackgroundSize backgroundSize = new BackgroundSize(Width, Height, false, false, false, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        gameBoxPane.setBackground(new Background(background));
        
        root.setCenter(gameBoxPane);
        
        // Top status bar
        HBox statusBar = createStatusBar();
        root.setTop(statusBar);
        
        // Left side plant selection
        VBox plantPanel = createPlantPanel();
        root.setLeft(plantPanel);
        
        // Right side player info
        VBox playerPanel = createPlayerInfoPanel();
        root.setRight(playerPanel);
        
        // Create scene and stage
        Scene scene = new Scene(root, Width + 200, Height + 50); // Extra space for UI
        stage.setTitle("Plants vs Zombies - Multiplayer (" + gameMode + ")");
        stage.setScene(scene);
        stage.setResizable(false);
        
        // Start status update timer
        startStatusUpdates();
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-text-fill: white;");
        
        connectionStatusLabel = new Label("🔗 Connected");
        connectionStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        gameStatusLabel = new Label("⏳ Waiting for game to start");
        gameStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        waveLabel = new Label("Wave: --");
        waveLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        playerStatusLabel = new Label("Plants: " + selectedPlants.size());
        playerStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        statusBar.getChildren().addAll(connectionStatusLabel, gameStatusLabel, waveLabel, playerStatusLabel);
        return statusBar;
    }
    
    private VBox createPlantPanel() {
        VBox plantPanel = new VBox(5);
        plantPanel.setPadding(new Insets(10));
        plantPanel.setPrefWidth(150);
        plantPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-text-fill: white;");
        
        Label titleLabel = new Label("Selected Plants");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");
        
        VBox plantList = new VBox(5);
        for (AbstractPlantGameObject.PlantType plantType : selectedPlants) {
            Label plantLabel = new Label("• " + plantType.name().replace("_", " "));
            plantLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            plantList.getChildren().add(plantLabel);
        }
        
        plantPanel.getChildren().addAll(titleLabel, plantList);
        return plantPanel;
    }
    
    private VBox createPlayerInfoPanel() {
        VBox playerPanel = new VBox(5);
        playerPanel.setPadding(new Insets(10));
        playerPanel.setPrefWidth(150);
        playerPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-text-fill: white;");
        
        Label titleLabel = new Label("Player Info");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");
        
        Label clientIdLabel = new Label("ID: " + (clientEngine != null ? clientEngine.getClientId() : "Unknown"));
        clientIdLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        Label gameModeLabel = new Label("Mode: " + gameMode);
        gameModeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        playerPanel.getChildren().addAll(titleLabel, clientIdLabel, gameModeLabel);
        return playerPanel;
    }
    
    private void startStatusUpdates() {
        // Update status every second
        Thread statusThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Platform.runLater(() -> {
                        if (clientEngine != null) {
                            // Update connection status
                            if (clientEngine.isConnected()) {
                                connectionStatusLabel.setText("🔗 Connected");
                                connectionStatusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px;");
                            } else {
                                connectionStatusLabel.setText("❌ Disconnected");
                                connectionStatusLabel.setStyle("-fx-text-fill: #F44336; -fx-font-size: 12px;");
                            }
                            
                            // Update game status
                            if (clientEngine.isGameStarted()) {
                                gameStatusLabel.setText("🎮 Game in progress");
                                gameStatusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px;");
                                waveLabel.setText("Wave: " + clientEngine.getCurrentWave());
                            } else if (clientEngine.isGameEnded()) {
                                gameStatusLabel.setText("🏁 Game ended");
                                gameStatusLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 12px;");
                            }
                        }
                    });
                    
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        
        statusThread.setDaemon(true);
        statusThread.start();
    }
    
    public Pane getGameBoxPane() {
        return gameBoxPane;
    }
    
    public void setIsShovelMode(boolean isShovelMode) {
        this.isShovelMode = isShovelMode;
    }
    
    public boolean getIsShovelMode() {
        return isShovelMode;
    }
}
