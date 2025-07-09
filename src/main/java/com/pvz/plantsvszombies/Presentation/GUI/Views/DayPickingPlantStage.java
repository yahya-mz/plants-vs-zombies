package com.pvz.plantsvszombies.GUI.Views;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.util.ArrayList;

public class DayPickingPlantStage {
    private final ArrayList<AbstractPlantGameObject.PlantType> selectedPlants = new ArrayList<>();
    private final HBox selectedPlantHBox = new HBox(0);
    private Button startBtn;

    private Image[] cardImages;

    public Stage createStage(Stage primaryStage) {
        startBtn = new Button("Start");
        startBtn.setDisable(true);

        VBox root = new VBox(5);
        root.setPadding(new Insets(20));

        Image backgroundImage = new Image(
                GlobalSettings.getResource("graphics/Items/Background/daypickingstage.png").toString()
        );
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

        HBox firstRow = createCardRow(0, 3);
        VBox.setMargin(firstRow, new Insets(110, 0, 0, 220));
        addHoverEffectToImages(firstRow);

        HBox secondRow = createCardRow(4, 7);
        VBox.setMargin(secondRow, new Insets(0, 0, 0, 220));
        addHoverEffectToImages(secondRow);

        VBox.setMargin(selectedPlantHBox, new Insets(5, 0, 0, 125));



        startBtn.setOnAction(e -> {
            DayView gameStage = DayView.createStage(selectedPlants);
            gameStage.show();
            gameStage.setOnHiding(event -> {
                primaryStage.show();
                Mediator.getInstance().stopEngine();
            });
            ((Stage) startBtn.getScene().getWindow()).close();
        });

        root.getChildren().addAll(firstRow, secondRow, selectedPlantHBox, startBtn);

        Scene scene = new Scene(root, 900, 600);
        Stage stage = new Stage();
        stage.setTitle("Picking Plant Stage");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        return stage;
    }

    private void loadCardImages() {
        File cardsDirectory = new File(GlobalSettings.getDir("graphics/Cards/Day"));
        File[] cardFiles = cardsDirectory.listFiles();
        cardImages = new Image[cardFiles.length];
        for (int j = 0; j < cardImages.length; j++) {
            cardImages[j] = new Image(cardFiles[j].getPath());
        }
    }

    public HBox createCardRow(int from, int to) {
        HBox row = new HBox(5);
        for (int i = from; i <= to; i++) {
            ImageView card = new ImageView(cardImages[i]);
            card.setFitWidth(100);
            card.setPreserveRatio(true);
            card.setCursor(Cursor.HAND);

            final int index = i;
            card.setOnMouseClicked(e -> {
                if (selectedPlants.size() < 6) {
                    String imgPath = cardImages[index].getUrl();
                    String imgName = imgPath.substring(imgPath.lastIndexOf('\\') + 1, imgPath.lastIndexOf('.'));
                    AbstractPlantGameObject.PlantType type = AbstractPlantGameObject.PlantType.valueOf(imgName);
                    selectedPlants.add(type);

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
                            startBtn.setDisable(selectedPlants.size() != 6);
                        }
                    });

                    selectedPlantHBox.getChildren().add(clonedCard);
                    updateCardMargins();


                    card.setDisable(true);
                    card.setOpacity(0.5);
                    startBtn.setDisable(selectedPlants.size() != 6);
                }
            });

            row.getChildren().add(card);
        }
        return row;
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

}