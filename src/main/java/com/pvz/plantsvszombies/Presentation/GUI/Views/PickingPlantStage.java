package com.pvz.plantsvszombies.Presentation.GUI.Views;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundManager;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundType;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class PickingPlantStage {
    private final ArrayList<AbstractPlantGameObject.PlantType> selectedPlants = new ArrayList<>();
    private final HBox selectedPlantHBox = new HBox(0);
    private Stage primaryStage;
    private Button playBtn;
    private final String _mode;
    Image backgroundImage;

    public PickingPlantStage(String mode){
        this._mode = mode;
    }

    private Image[] cardImages;

    public Stage createStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox root = new VBox(5);
        StackPane mainPickingPane = new StackPane();
        int topMargineForScroll;

        playBtn = createStartButton();
        StackPane.setAlignment(playBtn , Pos.BOTTOM_CENTER);
        StackPane.setMargin(playBtn, new Insets(0, 0, -50, 0));
        playBtn.setDisable(true);

        root.setPadding(new Insets(20));
        if (_mode.equals("day")){
            backgroundImage = new Image(
                    GlobalSettings.getResource("graphics/Items/Background/daypickingstage.png")
            );
        } else {
            backgroundImage = new Image(
                    GlobalSettings.getResource("graphics/Items/Background/nightpickingstage.png")
            );
        }

        BackgroundSize backgroundSize = new BackgroundSize(
                100, 100, true, true, true, false
        );

        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        root.setBackground(new Background(background));

        loadCardImages();

        ScrollPane cardScrollPane = createCardScrollPaneWithCards();
        if(_mode.equals("day")){topMargineForScroll = 108;}
        else{topMargineForScroll = 125;}
        VBox.setMargin(cardScrollPane, new Insets(topMargineForScroll, 0, 0, 180));
        VBox.setMargin(selectedPlantHBox, new Insets(10, 0, 0, 125));

        root.getChildren().addAll(cardScrollPane, selectedPlantHBox);
        mainPickingPane.getChildren().addAll(root, playBtn);

        Scene scene = new Scene(mainPickingPane, 900, 600);
        Stage stage = new Stage();
        stage.setTitle("Picking Plant Stage");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        return stage;
    }

    private void loadCardImages() {
        File cardsDirectory = new File(GlobalSettings.getDir("graphics/Cards"));
        File[] cardFiles = cardsDirectory.listFiles();
        cardImages = new Image[cardFiles.length];
        for (int j = 0; j < cardImages.length; j++) {
            cardImages[j] = new Image(cardFiles[j].getPath());
        }
    }

//    public HBox createCardRow(int from, int to) {
//        HBox row = new HBox(8);
//        for (int i = from; i <= to; i++) {
//            ImageView card = new ImageView(cardImages[i]);
//            card.setFitWidth(100);
//            card.setPreserveRatio(true);
//            card.setCursor(Cursor.HAND);
//
//            final int index = i;
//            card.setOnMouseClicked(e -> {
//                if (selectedPlants.size() < 6) {
//                    String imgPath = cardImages[index].getUrl();
//                    String imgName = imgPath.substring(imgPath.lastIndexOf('\\') + 1, imgPath.lastIndexOf('.'));
//                    AbstractPlantGameObject.PlantType type = AbstractPlantGameObject.PlantType.valueOf(imgName);
//                    selectedPlants.add(type);
//                    playBtn.setDisable(selectedPlants.size() != 6);
//
//                    ImageView clonedCard = new ImageView(cardImages[index]);
//                    clonedCard.setFitWidth(100);
//                    clonedCard.setPreserveRatio(true);
//                    clonedCard.setCursor(Cursor.HAND);
//
//                    clonedCard.setOnMouseEntered(ev -> {
//                        clonedCard.setTranslateY(-10);
//                        clonedCard.setScaleX(1.05);
//                        clonedCard.setScaleY(1.05);
//                    });
//                    clonedCard.setOnMouseExited(ev -> {
//                        clonedCard.setTranslateY(0);
//                        clonedCard.setScaleX(1);
//                        clonedCard.setScaleY(1);
//                    });
//
//
//                    clonedCard.setOnMouseClicked(event -> {
//                        if (event.getButton().equals(MouseButton.PRIMARY)) {
//                            selectedPlantHBox.getChildren().remove(selectedPlants.indexOf(type));
//                            selectedPlants.remove(type);
//
//                            updateCardMargins();
//
//                            card.setOpacity(1);
//                            card.setDisable(false);
//                            playBtn.setDisable(selectedPlants.size() != 6);
//                        }
//                    });
//
//                    selectedPlantHBox.getChildren().add(clonedCard);
//                    updateCardMargins();
//
//
//                    card.setDisable(true);
//                    card.setOpacity(0.5);
//                    playBtn.setDisable(selectedPlants.size() != 6);
//                }
//            });
//
//            row.getChildren().add(card);
//        }
//        return row;
//    }


    public ScrollPane createCardScrollPaneWithCards() {
        VBox cardVBox = new VBox(10);
        cardVBox.setPadding(new Insets(20, 0, 0, 0));
        cardVBox.setStyle("-fx-background-color: transparent;");
        cardVBox.setFillWidth(true);
        cardVBox.setBackground(Background.EMPTY);
        int maxhight;
        int maxwidth;

        if (_mode.equals("day")){maxhight = 200; maxwidth = 510;}
        else{maxhight = 184; maxwidth = 510; }

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
        scrollPane.setMaxHeight(maxhight);
        scrollPane.setMaxWidth(maxwidth);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        String css = """
        .custom-scroll-pane .scroll-bar {
            -fx-background-color: transparent;
        }

        .custom-scroll-pane .scroll-bar:vertical .thumb {
            -fx-background-color: #4A2C00;
            -fx-background-insets: 2;
            -fx-background-radius: 5;
        }

        .custom-scroll-pane .scroll-bar:horizontal .thumb {
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

        // افزودن CSS به صحنه
        Scene scene = scrollPane.getScene();
        if (scene != null) {
            scene.getStylesheets().add("data:text/css," + css);
        } else {
            scrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.getStylesheets().add("data:text/css," + css);
                }
            });
        }

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
                playBtn.setDisable(selectedPlants.size() != 6);

                ImageView clonedCard = new ImageView(cardImages[index]);
                clonedCard.setFitWidth(100);
                clonedCard.setPreserveRatio(true);
                clonedCard.setCursor(Cursor.HAND);

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

                clonedCard.setOnMouseClicked(event -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        selectedPlantHBox.getChildren().remove(selectedPlants.indexOf(type));
                        selectedPlants.remove(type);
                        updateCardMargins();

                        card.setOpacity(1);
                        card.setDisable(false);
                        playBtn.setDisable(selectedPlants.size() != 6);
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
        for (Node node : hbox.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;

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
        }
    }

    private void updateCardMargins() {
        for (int i = 0; i < selectedPlantHBox.getChildren().size(); i++) {
            var node = selectedPlantHBox.getChildren().get(i);
            Insets margin;
            switch (i) {
                case 0 -> margin = new Insets(0, 2, 0, 0);
                case 1 -> margin = new Insets(0, 1, 0, 0);
                case 2 -> margin = new Insets(0, 0, 0, 0);
                case 3 -> margin = new Insets(0, 0, 0, 0);
                case 4 -> margin = new Insets(0, 0, 0, 0);
                default -> margin = new Insets(0, 0, 0, 0);
            }
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
            Stage gameStage;
            if (_mode.equals("day")){
                gameStage = DayView.createStage(selectedPlants);
//                SoundManager.play(SoundType.BACKGROUND);
            }
            else {
                gameStage = NightView.createStage(selectedPlants);
                SoundManager.play(SoundType.NIGHT_BACKGROUND);
            }
            gameStage.show();
            gameStage.setOnHiding(event -> {
                primaryStage.show();
                Mediator.getInstance().stopGameEngine();
            });
            ((Stage) playBtn.getScene().getWindow()).close();
        });

        return button;
    }
}