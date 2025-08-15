package com.pvz.plantsvszombies.Presentation.GUI.Views;

import java.util.ArrayList;
import java.util.List;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import com.pvz.plantsvszombies.Multiplayer.Engines.ClientGameEngine;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.*;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Main game view for multiplayer mode - follows NightView pattern
 */
public class MultiplayerGameView extends AbstractLevelView {
    
    public static final double Width = 1200;
    public static final double Height = 728;
    
    private ClientGameEngine clientEngine;
    private String serverAddress;
    private String gameMode;
    private List<AbstractPlantGameObject.PlantType> selectedPlants;
    

    
    // Game components
    private static StackPane bottommostPlane;
    private static final BooleanProperty isShovelMode = new SimpleBooleanProperty(false);
    private static final IntegerProperty counterValue = new SimpleIntegerProperty(0);
    
    // Visual engine reference
    private Object visualEngine;
    
    private MultiplayerGameView() {
    }
    
    public static MultiplayerGameView createStage(List<AbstractPlantGameObject.PlantType> selectedPlants, 
                                   String serverAddress, String gameMode) {
        var view = new MultiplayerGameView();
        view.selectedPlants = selectedPlants;
        view.serverAddress = serverAddress;
        view.gameMode = gameMode;
        
        bottommostPlane = new StackPane();
        view._gameBoxPane = bottommostPlane;
        
        // Create UI components
        VBox suncounter = view.createCounter();
        HBox plantbar = view.createTopPlantSelectionBar(selectedPlants);
        Button shoveltool = view.createShovelToolButton();
        Button pause = view.createPauseButton();
        
        // Create main bar
        HBox mainbar = new HBox(80);
        mainbar.setMaxWidth(1100);
        mainbar.setMaxHeight(100);
        mainbar.getChildren().addAll(suncounter, plantbar, shoveltool);
        mainbar.setAlignment(Pos.TOP_CENTER);
        mainbar.setMouseTransparent(false);
        mainbar.setPickOnBounds(true);
        HBox.setMargin(suncounter, new Insets(73, 60, 0, 236));
        HBox.setMargin(plantbar, new Insets(0,85,0, -70));
        HBox.setMargin(shoveltool, new Insets(-14,0,0,-30));
        mainbar.setMinWidth(50);
        mainbar.setMinHeight(30);
        
        bottommostPlane.getChildren().addAll(mainbar, pause);
        StackPane.setAlignment(pause, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(mainbar, Pos.TOP_CENTER);
        StackPane.setMargin(mainbar, new Insets(15, 60, 185,-20));
        
        var scene = new Scene(bottommostPlane, Width, Height);
        view.setScene(scene);
        view.setResizable(false);
        view.setupEngines();
        
        return view;
    }
    
    /**
     * Create stage with an already-connected ClientGameEngine
     */
    public static MultiplayerGameView createStage(ClientGameEngine connectedClientEngine, 
                                   List<AbstractPlantGameObject.PlantType> selectedPlants, 
                                   String serverAddress, String gameMode) {
        var view = new MultiplayerGameView();
        view.clientEngine = connectedClientEngine;
        view.selectedPlants = selectedPlants;
        view.serverAddress = serverAddress;
        view.gameMode = gameMode;
        
        bottommostPlane = new StackPane();
        view._gameBoxPane = bottommostPlane;
        
        // Create UI components
        VBox suncounter = view.createCounter();
        HBox plantbar = view.createTopPlantSelectionBar(selectedPlants);
        Button shoveltool = view.createShovelToolButton();
        Button pause = view.createPauseButton();
        
        // Create main bar
        HBox mainbar = new HBox(80);
        mainbar.setMaxWidth(1100);
        mainbar.setMaxHeight(100);
        mainbar.getChildren().addAll(suncounter, plantbar, shoveltool);
        mainbar.setAlignment(Pos.TOP_CENTER);
        mainbar.setMouseTransparent(false);
        mainbar.setPickOnBounds(true);
        HBox.setMargin(suncounter, new Insets(73, 60, 0, 236));
        HBox.setMargin(plantbar, new Insets(0,85,0, -70));
        HBox.setMargin(shoveltool, new Insets(-14,0,0,-30));
        mainbar.setMinWidth(50);
        mainbar.setMinHeight(30);
        
        bottommostPlane.getChildren().addAll(mainbar, pause);
        StackPane.setAlignment(pause, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(mainbar, Pos.TOP_CENTER);
        StackPane.setMargin(mainbar, new Insets(15, 60, 185,-20));
        
        var scene = new Scene(bottommostPlane, Width, Height);
        view.setScene(scene);
        view.setResizable(false);
        view.setupEngines();
        
        return view;
    }
    
    public static VBox createCounter() {
        Label counterLabel = new Label();
        counterLabel.textProperty().bind(counterValue.asString());
        counterLabel.setId("counter-label");

        VBox counterBox = new VBox(counterLabel);
        counterBox.setAlignment(Pos.TOP_CENTER);
        counterBox.setPrefHeight(100);
        counterBox.setMinWidth(50);

        return counterBox;
    }

    public int setMyCounterValue() {
        counterValue.set(0);
        return counterValue.get();
    }

    public HBox createTopPlantSelectionBar(List<AbstractPlantGameObject.PlantType> selectedPlants) {
        HBox buttonBar = new HBox(0);

        buttonBar.setAlignment(Pos.TOP_CENTER);
        buttonBar.setPrefHeight(86);

        for (int i = 0; i < selectedPlants.size(); i++) {
            Button btn = new Button("Item " + i);
            btn.setCursor(Cursor.HAND);
            Image path = new Image(GlobalSettings.getResource("graphics/Cards/" + selectedPlants.get(i).name() + ".png"), true);
            ImageView cardpic = new ImageView(path);

            cardpic.setPreserveRatio(true);
            cardpic.setSmooth(true);
            cardpic.setFitWidth(79);

            Insets margin;
            switch (i) {
                case 0 -> margin = new Insets(5, 4, 0, 40);
                case 1 -> margin = new Insets(5, 0, 0, 0);
                case 2 -> margin = new Insets(5, 3, 0, 5);
                case 3 -> margin = new Insets(5, 25, 0, 0);
                case 4 -> margin = new Insets(5, 4, 0, -20);
                case 5 -> margin = new Insets(5, 8, 0, 0);
                default -> margin = new Insets(5, 0, 0, 0);
            }
            HBox.setMargin(btn, margin);

            final AbstractPlantGameObject.PlantType final_plantType = selectedPlants.get(i);
            btn.setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    // TODO: Implement plant selection for multiplayer
                    System.out.println("Selected plant: " + final_plantType);
                }
            });

            btn.setGraphic(cardpic);
            btn.setPrefSize(70, 90);
            btn.setMinSize(70, 90);
            btn.setMaxSize(70, 90);
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");

            buttonBar.getChildren().add(btn);
        }
        addHoverEffectToImages(buttonBar);
        return buttonBar;
    }

    public static void addHoverEffectToImages(HBox hbox) {
        for (Node node : hbox.getChildren()) {
            if (node instanceof Button btn) {
                btn.setOnMouseEntered(e -> {
                    btn.setTranslateY(-9);
                    btn.setScaleX(1.02);
                    btn.setScaleY(1.02);
                });

                btn.setOnMouseExited(e -> {
                    btn.setTranslateY(0);
                    btn.setScaleX(1);
                    btn.setScaleY(1);
                });
            }
        }
    }

    public Button createShovelToolButton() {
        Image shovelCursorImage = new Image(GlobalSettings.getResource("graphics/Items/bar/shoveltool/shovel.png"));
        Image shovelFullImage = new Image(GlobalSettings.getResource("graphics/Items/bar/shoveltool/shovelToolFull.png"), true);
        Image shovelEmptyImage = new Image(GlobalSettings.getResource("graphics/Items/bar/shoveltool/shovelToolEmpty.png"), true);

        Button shovelButton = new Button();
        ImageView shovelFullView = new ImageView(shovelFullImage);
        ImageView shovelEmptyView = new ImageView(shovelEmptyImage);

        shovelBarView(shovelButton, shovelFullView);

        isShovelModeProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && shovelButton.getScene() != null) {
                Platform.runLater(() -> {
                    shovelButton.getScene().setCursor(Cursor.DEFAULT);
                    shovelBarView(shovelButton, new ImageView(shovelFullImage));
                });
            }
        });

        shovelButton.setOnAction(e -> {
            isShovelMode.set(true);

            Scene scene = shovelButton.getScene();
            if (scene != null && getIsShovelMode()) {
                scene.setCursor(new ImageCursor(shovelCursorImage, 32, 32));
                // TODO: Implement shovel functionality for multiplayer
                System.out.println("Shovel mode activated");
                shovelBarView(shovelButton, new ImageView(shovelEmptyImage));

                scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY && getIsShovelMode()) {
                        // TODO: Implement shovel deactivation for multiplayer
                        System.out.println("Shovel mode deactivated");
                        isShovelMode.set(false);
                    }
                });
            }
        });
        addHoverEffectToShovel(shovelButton);

        return shovelButton;
    }

    private void addHoverEffectToShovel(Button btn) {
        btn.setOnMouseEntered(e -> {
            if (!getIsShovelMode()) {
                btn.setScaleX(1.1);
                btn.setScaleY(1.1);
            }
        });

        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
    }

    public static BooleanProperty isShovelModeProperty() {
        return isShovelMode;
    }

    public void setIsShovelMode(boolean value) {
        isShovelMode.set(value);
    }

    public static boolean getIsShovelMode() {
        return isShovelMode.get();
    }

    private static void shovelBarView(Button btn, ImageView shovelView) {
        shovelView.setFitWidth(120);
        shovelView.setPreserveRatio(false);
        btn.setGraphic(shovelView);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");
    }

    public static Button createPauseButton() {
        Button PauseButton = new Button();
        // TODO: Implement pause functionality for multiplayer
        PauseButton.setText("PAUSE");
        PauseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        PauseButton.setPrefSize(80, 40);
        
        return PauseButton;
    }

    private void setupEngines() {
        try {
            // Don't create a new client engine - use the one passed in
            if (clientEngine == null) {
                throw new IllegalStateException("ClientGameEngine must be provided and connected before creating MultiplayerGameView");
            }
            
            // Start the client engine if it hasn't been started yet
            if (!clientEngine.isConnected()) {
                clientEngine.start();
                
                // Wait a moment for connection to establish
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Ignore interruption
                }
                
                // Send ready status to server with selected plants
                if (clientEngine.isConnected()) {
                    clientEngine.sendReadyStatus(selectedPlants);
                    System.out.println("Sent ready status to server with plants: " + selectedPlants);
                }
            }
            
            // Initialize mediator
            Mediator.init(clientEngine, null);
            
            // Start game loop for client engine
            startGameLoop();
            
            // Setup close handler
            this.setOnHiding((event) -> {
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
    

    

    

    

    

    
    private void startGameLoop() {
        System.out.println("🚀 Starting game loop for multiplayer...");
        // Start game loop for client engine at 30 FPS
        Thread gameThread = new Thread(() -> {
            int loopCount = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    loopCount++;
                    if (clientEngine != null && clientEngine.isConnected()) {
                        clientEngine.update();
                        if (loopCount % 30 == 0) { // Log every second (30 FPS)
                            System.out.println("🔄 Game loop running - tick: " + loopCount + ", client connected: " + clientEngine.isConnected());
                        }
                    } else {
                        if (loopCount % 30 == 0) { // Log every second
                            System.out.println("⚠️ Game loop running but client engine is null or not connected");
                            System.out.println("   clientEngine: " + (clientEngine != null ? "exists" : "null"));
                            System.out.println("   isConnected: " + (clientEngine != null ? clientEngine.isConnected() : "N/A"));
                        }
                    }
                    Thread.sleep(33); // ~30 FPS (1000ms / 30 = 33ms)
                } catch (InterruptedException e) {
                    System.out.println("🛑 Game loop interrupted");
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Error in client game loop: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("🛑 Game loop stopped");
        });
        
        gameThread.setDaemon(true);
        gameThread.setName("MultiplayerGameLoop");
        gameThread.start();
        System.out.println("✅ Game loop started successfully");
    }
    
    public Pane getGameBoxPane() {
        return _gameBoxPane;
    }
}
