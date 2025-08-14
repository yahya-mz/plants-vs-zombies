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
    
    private void setupStage(Stage stage) {
        try {
            // Initialize client engine
            clientEngine = new ClientGameEngine(Width, Height, serverAddress, gameMode);
            
            // Initialize visual engine based on game mode (simplified for now)
            // TODO: Properly integrate with visual engines
            
            // Initialize mediator
            Mediator.init(clientEngine, null);
            
            // Setup UI
            setupUI(stage);
            
            // Start game engines
            Mediator.getInstance().startGameEngine();
            
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
        
        connectionStatusLabel = new Label("🔗 Connecting...");
        connectionStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        gameStatusLabel = new Label("⏳ Waiting for game to start");
        gameStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        waveLabel = new Label("Wave: --");
        waveLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        statusBar.getChildren().addAll(connectionStatusLabel, gameStatusLabel, waveLabel);
        return statusBar;
    }
    
    private VBox createPlantPanel() {
        VBox plantPanel = new VBox(5);
        plantPanel.setPadding(new Insets(10));
        plantPanel.setPrefWidth(100);
        plantPanel.setStyle("-fx-background-color: rgba(139, 69, 19, 0.8);");
        
        Label titleLabel = new Label("🌱 Plants");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        plantPanel.getChildren().add(titleLabel);
        
        // Add selected plants (simplified for now)
        for (AbstractPlantGameObject.PlantType plantType : selectedPlants) {
            Label plantLabel = new Label(plantType.name());
            plantLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
            plantPanel.getChildren().add(plantLabel);
        }
        
        return plantPanel;
    }
    
    private VBox createPlayerInfoPanel() {
        VBox playerPanel = new VBox(10);
        playerPanel.setPadding(new Insets(10));
        playerPanel.setPrefWidth(100);
        playerPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        
        Label titleLabel = new Label("👥 Player Info");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        playerStatusLabel = new Label("Status: --");
        playerStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        playerPanel.getChildren().addAll(titleLabel, playerStatusLabel);
        return playerPanel;
    }
    
    private void startStatusUpdates() {
        Thread statusThread = new Thread(() -> {
            while (clientEngine != null && !clientEngine.isGameEnded()) {
                Platform.runLater(this::updateStatusLabels);
                try {
                    Thread.sleep(1000); // Update every second
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        statusThread.setDaemon(true);
        statusThread.start();
    }
    
    private void updateStatusLabels() {
        if (clientEngine == null) return;
        
        // Update connection status
        if (clientEngine.isConnected()) {
            connectionStatusLabel.setText("✅ Connected");
            connectionStatusLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 12px;");
        } else {
            connectionStatusLabel.setText("❌ Disconnected");
            connectionStatusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        }
        
        // Update game status
        if (clientEngine.isGameEnded()) {
            if (clientEngine.isPlayerLost()) {
                gameStatusLabel.setText("💀 You Lost");
                gameStatusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            } else {
                gameStatusLabel.setText("🎉 Game Completed");
                gameStatusLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 12px;");
            }
        } else if (clientEngine.isGameStarted()) {
            gameStatusLabel.setText("🎮 Playing");
            gameStatusLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 12px;");
        } else {
            gameStatusLabel.setText("⏳ Waiting for players");
            gameStatusLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 12px;");
        }
        
        // Update wave
        waveLabel.setText("Wave: " + clientEngine.getCurrentWave());
        
        // Update player info
        String clientIdDisplay = clientEngine.getClientId() != null ? 
            clientEngine.getClientId().substring(0, Math.min(8, clientEngine.getClientId().length())) + "..." : 
            "Unknown";
        String playerInfo = String.format("ID: %s\nZombies Killed: %d", 
            clientIdDisplay, 
            clientEngine.getZombiesKilled());
        playerStatusLabel.setText(playerInfo);
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
