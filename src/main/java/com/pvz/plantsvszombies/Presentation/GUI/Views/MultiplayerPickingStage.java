package com.pvz.plantsvszombies.Presentation.GUI.Views;

import java.io.File;
import java.util.ArrayList;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Multiplayer.Engines.ClientGameEngine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Plant selection screen for multiplayer mode
 */
public class MultiplayerPickingStage {
    private final ArrayList<AbstractPlantGameObject.PlantType> selectedPlants = new ArrayList<>();
    private final HBox selectedPlantHBox = new HBox(0);
    private Stage primaryStage;
    private Button playBtn;
    private final String serverAddress;
    private final String gameMode;
    private Image[] cardImages;
    
    // Status display
    private Label statusLabel;
    
    // Client engine for sending ready status
    private ClientGameEngine clientEngine;
    
    public MultiplayerPickingStage(String serverAddress, String gameMode) {
        this.serverAddress = serverAddress;
        this.gameMode = gameMode;
        
        // Initialize client engine for sending ready status
        try {
            this.clientEngine = new ClientGameEngine(900, 600, serverAddress, gameMode);
            this.clientEngine.start();
        } catch (Exception e) {
            System.err.println("Failed to initialize client engine: " + e.getMessage());
        }
    }
    
    public Stage createStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox root = new VBox(5);
        StackPane mainPickingPane = new StackPane();
        
        // Status label for connection info
        statusLabel = new Label("🔗 Connecting to server: " + serverAddress);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-padding: 10;");
        
        playBtn = createStartButton();
        
        StackPane.setAlignment(playBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(playBtn, new Insets(0, 0, -50, 0));
        playBtn.setDisable(true);
        
        root.setPadding(new Insets(20));
        
        // Background
        Image backgroundImage = new Image(
            GlobalSettings.getResource("graphics/Items/Background/daypickingstage.png")
        );
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(background));
        
        // Load card images
        loadCardImages();
        
        // Create scrollable card area
        ScrollPane cardScrollPane = createCardScrollPaneWithCards();
        VBox.setMargin(cardScrollPane, new Insets(80, 0, 0, 180));
        VBox.setMargin(selectedPlantHBox, new Insets(10, 0, 0, 125));
        VBox.setMargin(statusLabel, new Insets(0, 0, 0, 20));
        
        root.getChildren().addAll(statusLabel, cardScrollPane, selectedPlantHBox);
        mainPickingPane.getChildren().addAll(root, playBtn);
        
        Scene scene = new Scene(mainPickingPane, 900, 600);
        Stage stage = new Stage();
        stage.setTitle("Multiplayer Plant Selection");
        stage.setScene(scene);
        stage.setResizable(false);
        
        // Update status periodically
        updateConnectionStatus();
        
        return stage;
    }
    
    private void updateConnectionStatus() {
        // Check connection status and update UI
        if (clientEngine != null && clientEngine.isConnected()) {
            if (selectedPlants.size() == 6) {
                playBtn.setDisable(false);
                statusLabel.setText("✅ Connected! Ready to start with all plants selected.");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-padding: 10;");
            } else {
                playBtn.setDisable(true);
                statusLabel.setText("🌱 Connected! Selected: " + selectedPlants.size() + "/6 plants");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF9800; -fx-padding: 10;");
            }
        } else {
            playBtn.setDisable(true);
            statusLabel.setText("🔄 Connecting to server: " + serverAddress);
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-padding: 10;");
        }
    }
    
    private void loadCardImages() {
        // Load plant card images
        cardImages = new Image[6];
        String[] imagePaths = {
            "graphics/Items/Plants/peashooter/peashooter.png",
            "graphics/Items/Plants/sunflower/sunflower.png",
            "graphics/Items/Plants/wallnut/wallnut.png",
            "graphics/Items/Plants/chomper/chomper.png",
            "graphics/Items/Plants/repeater/repeater.png",
            "graphics/Items/Plants/snowpea/snowpea.png"
        };
        
        for (int i = 0; i < 6; i++) {
            cardImages[i] = new Image(GlobalSettings.getResource(imagePaths[i]));
        }
    }
    
    private ScrollPane createCardScrollPaneWithCards() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        HBox cardContainer = new HBox(10);
        cardContainer.setAlignment(Pos.CENTER);
        
        // Create plant cards
        AbstractPlantGameObject.PlantType[] plantTypes = {
            AbstractPlantGameObject.PlantType.PEASHOOTER,
            AbstractPlantGameObject.PlantType.SUNFLOWER,
            AbstractPlantGameObject.PlantType.WALL_NUT,
            AbstractPlantGameObject.PlantType.CHERRY_BOMB,
            AbstractPlantGameObject.PlantType.REPEATER,
            AbstractPlantGameObject.PlantType.SNOW_PEA
        };
        
        for (int i = 0; i < 6; i++) {
            HBox card = createPlantCard(plantTypes[i], cardImages[i]);
            cardContainer.getChildren().add(card);
        }
        
        scrollPane.setContent(cardContainer);
        return scrollPane;
    }
    
    private HBox createPlantCard(AbstractPlantGameObject.PlantType plantType, Image image) {
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER);
        card.setCursor(Cursor.HAND);
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 10; -fx-padding: 10;");
        
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        
        card.getChildren().add(imageView);
        
        card.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (selectedPlants.contains(plantType)) {
                    // Remove plant if already selected
                    selectedPlants.remove(plantType);
                    selectedPlantHBox.getChildren().removeIf(node -> {
                        if (node instanceof HBox hbox) {
                            return hbox.getUserData() == plantType;
                        }
                        return false;
                    });
                    updateCardMargins();
                } else if (selectedPlants.size() < 6) {
                    // Add plant if not at limit
                    selectedPlants.add(plantType);
                    
                    // Create selected plant display
                    HBox clonedCard = new HBox();
                    clonedCard.setAlignment(Pos.CENTER);
                    clonedCard.setStyle("-fx-background-color: rgba(76, 175, 80, 0.8); -fx-background-radius: 8; -fx-padding: 5;");
                    clonedCard.setUserData(plantType);
                    
                    ImageView clonedImageView = new ImageView(image);
                    clonedImageView.setFitWidth(60);
                    clonedImageView.setFitHeight(60);
                    clonedImageView.setPreserveRatio(true);
                    
                    clonedCard.getChildren().add(clonedImageView);
                    
                    // Add click to remove functionality
                    clonedCard.setOnMouseClicked(removeEvent -> {
                        if (removeEvent.getButton() == MouseButton.PRIMARY) {
                            selectedPlants.remove(plantType);
                            selectedPlantHBox.getChildren().remove(clonedCard);
                            updateCardMargins();
                            updateConnectionStatus();
                        }
                    });
                    
                    // Update status
                    updateConnectionStatus();
                }
                
                updateConnectionStatus();
            }
        });
        
        addHoverEffectToImages(card);
        
        return card;
    }
    
    public void addHoverEffectToImages(HBox hbox) {
        hbox.getChildren().forEach(node -> {
            if (node instanceof ImageView imageView) {
                imageView.setOnMouseEntered(e -> {
                    imageView.setTranslateY(-9);
                    imageView.setScaleX(1.02);
                    imageView.setScaleY(1.02);
                });
                
                imageView.setOnMouseExited(e -> {
                    imageView.setTranslateY(0);
                    imageView.setScaleX(1);
                    imageView.setScaleY(1);
                });
            }
        });
    }
    
    private void updateCardMargins() {
        for (int i = 0; i < selectedPlantHBox.getChildren().size(); i++) {
            var node = selectedPlantHBox.getChildren().get(i);
            Insets margin = switch (i) {
                case 0 -> new Insets(0, 2, 0, 0);
                case 1 -> new Insets(0, 1, 0, 0);
                default -> new Insets(0, 0, 0, 0);
            };
            HBox.setMargin(node, margin);
        }
    }
    
    public Button createStartButton() {
        double width = 250;
        double height = 250;
        String[] imagePath = {
            "graphics/Items/Buttons/play/normalplay.png",
            "graphics/Items/Buttons/play/onactionplay.png"
        };
        
        Image defaultImage = new Image(GlobalSettings.getResource(imagePath[0]));
        Image hoverImage = new Image(GlobalSettings.getResource(imagePath[1]));
        
        ImageView imageView = new ImageView(defaultImage);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        
        Button button = new Button();
        button.setMinSize(width, height);
        button.setMaxSize(width, height);
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        
        button.setOnMouseEntered(e -> {
            ImageView hoverImageView = new ImageView(hoverImage);
            hoverImageView.setFitWidth(width);
            hoverImageView.setFitHeight(height);
            hoverImageView.setPreserveRatio(true);
            button.setGraphic(hoverImageView);
        });
        
        button.setOnMouseExited(e -> {
            ImageView normalImageView = new ImageView(defaultImage);
            normalImageView.setFitWidth(width);
            normalImageView.setFitHeight(height);
            normalImageView.setPreserveRatio(true);
            button.setGraphic(normalImageView);
        });
        
        button.setOnAction(e -> {
            // Send ready status to server with selected plants
            if (selectedPlants.size() == 6) {
                // Send ready status to server
                if (clientEngine != null) {
                    clientEngine.sendReadyStatus(selectedPlants);
                    System.out.println("Sent ready status to server with plants: " + selectedPlants);
                }
                
                // Launch multiplayer game
                Stage gameStage = MultiplayerGameView.createStage(selectedPlants, serverAddress, gameMode);
                gameStage.show();
                gameStage.setOnHiding(event -> {
                    primaryStage.show();
                });
                ((Stage) button.getScene().getWindow()).close();
            } else {
                // Show error message
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Plant Selection Required");
                alert.setHeaderText("Please select 6 plants");
                alert.setContentText("You must select exactly 6 plants before starting the game.");
                alert.showAndWait();
            }
        });
        
        return button;
    }
}

