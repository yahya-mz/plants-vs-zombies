package com.pvz.plantsvszombies.Presentation.GUI.Views;

import java.io.File;
import java.util.ArrayList;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.GlobalSettings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
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
    
    public MultiplayerPickingStage(String serverAddress, String gameMode) {
        this.serverAddress = serverAddress;
        this.gameMode = gameMode;
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
        // This would be updated by the actual connection logic
        statusLabel.setText("✅ Connected! Select 6 plants and wait for other players.");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green; -fx-padding: 10;");
    }
    
    private void loadCardImages() {
        File cardsDirectory = new File(GlobalSettings.getDir("graphics/Cards"));
        File[] cardFiles = cardsDirectory.listFiles();
        cardImages = new Image[cardFiles.length];
        for (int j = 0; j < cardImages.length; j++) {
            cardImages[j] = new Image(cardFiles[j].getPath());
        }
    }
    
    public ScrollPane createCardScrollPaneWithCards() {
        VBox cardVBox = new VBox(10);
        cardVBox.setPadding(new Insets(20, 0, 0, 0));
        cardVBox.setStyle("-fx-background-color: transparent;");
        cardVBox.setFillWidth(true);
        cardVBox.setBackground(Background.EMPTY);
        
        int cardsPerRow = 4;
        for (int i = 0; i < cardImages.length; i += cardsPerRow) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER);
            for (int j = i; j < i + cardsPerRow && j < cardImages.length; j++) {
                ImageView card = createSelectableCard(j);
                row.getChildren().add(card);
            }
            addHoverEffectToImages(row);
            cardVBox.getChildren().add(row);
        }
        
        ScrollPane scrollPane = new ScrollPane(cardVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxHeight(200);
        scrollPane.setMaxWidth(510);
        scrollPane.getStyleClass().add("custom-scroll-pane");
        
        // Apply custom styling
        String css = """
        .custom-scroll-pane .scroll-bar {
            -fx-background-color: transparent;
        }
        .custom-scroll-pane .scroll-bar:vertical .thumb {
            -fx-background-color: #4A2C00;
            -fx-background-insets: 2;
            -fx-background-radius: 5;
        }
        .custom-scroll-pane .scroll-bar .track {
            -fx-background-color: transparent;
        }
        """;
        
        scrollPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 0;" +
            "-fx-control-inner-background: transparent;" +
            "-fx-background: transparent;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );
        
        scrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add("data:text/css," + css);
            }
        });
        
        return scrollPane;
    }
    
    private ImageView createSelectableCard(int index) {
        ImageView card = new ImageView(cardImages[index]);
        card.setFitWidth(100);
        card.setPreserveRatio(true);
        card.setCursor(Cursor.HAND);
        
        card.setOnMouseClicked(e -> {
            if (selectedPlants.size() < 6) {
                String imgPath = cardImages[index].getUrl();
                String imgName = imgPath.substring(imgPath.lastIndexOf('\\') + 1, imgPath.lastIndexOf('.'));
                AbstractPlantGameObject.PlantType type = AbstractPlantGameObject.PlantType.valueOf(imgName);
                selectedPlants.add(type);
                
                // Enable play button when 6 plants selected
                playBtn.setDisable(selectedPlants.size() != 6);
                
                // Update status
                statusLabel.setText("🌱 Selected: " + selectedPlants.size() + "/6 plants");
                
                ImageView clonedCard = new ImageView(cardImages[index]);
                clonedCard.setFitWidth(100);
                clonedCard.setPreserveRatio(true);
                clonedCard.setCursor(Cursor.HAND);
                
                // Add hover effects to cloned card
                clonedCard.setOnMouseEntered(ev -> {
                    clonedCard.setTranslateY(-10);
                    clonedCard.setScaleX(1.05);
                    clonedCard.setScaleY(1.05);
                });
                clonedCard.setOnMouseExited(ev -> {
                    clonedCard.setTranslateY(0);
                    clonedCard.setScaleX(1);
                    clonedCard.setScaleY(1);
                });
                
                // Remove plant on click
                clonedCard.setOnMouseClicked(event -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        selectedPlantHBox.getChildren().remove(selectedPlants.indexOf(type));
                        selectedPlants.remove(type);
                        updateCardMargins();
                        
                        card.setOpacity(1);
                        card.setDisable(false);
                        playBtn.setDisable(selectedPlants.size() != 6);
                        
                        // Update status
                        statusLabel.setText("🌱 Selected: " + selectedPlants.size() + "/6 plants");
                    }
                });
                
                selectedPlantHBox.getChildren().add(clonedCard);
                updateCardMargins();
                
                card.setDisable(true);
                card.setOpacity(0.5);
            }
        });
        
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
            // Launch multiplayer game
            Stage gameStage = MultiplayerGameView.createStage(selectedPlants, serverAddress, gameMode);
            gameStage.show();
            gameStage.setOnHiding(event -> {
                primaryStage.show();
            });
            ((Stage) button.getScene().getWindow()).close();
        });
        
        return button;
    }
}

