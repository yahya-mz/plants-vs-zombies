package com.pvz.plantsvszombies.Presentation.GUI.Views;

import com.pvz.plantsvszombies.GlobalSettings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DayMenu {

    private StackPane root;
    private VBox menuBox;
    private Scene scene;

    public Scene getScene() {
        if (scene == null) {
            scene = createScene();
        }
        return scene;
    }

    public Scene createScene() {
        double width = 700;
        double height = 500;

        Rectangle backgroundColor = new Rectangle(width, height, Color.BLACK);
        Image backgroundImage = new Image(GlobalSettings.getResource("graphics/Items/Background/daypause.png").toString());

        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(width);
        backgroundImageView.setFitHeight(height);
        backgroundImageView.setPreserveRatio(false);

        menuBox = new VBox();
        menuBox.setSpacing(20);
        menuBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(backgroundColor ,backgroundImageView, menuBox);

        Scene scene = new Scene(root, width, height);
        scene.setFill(Color.TRANSPARENT); // صحنه شفاف برای حذف پس‌زمینه سفید

        return scene;
    }


    public void applyState(String state) {
        menuBox.getChildren().clear();
        ImageView headerImage = new ImageView();
        headerImage.setFitHeight(100);
        headerImage.setPreserveRatio(true);

        HBox buttonBox = new HBox() ;
        buttonBox.setSpacing(25);
        buttonBox.setAlignment(Pos.CENTER);

        switch (state) {
            case "pause" -> {
                headerImage = createHeaderImage("pause");
                Button exit = createImageButton("back");
                Button restart = createImageButton("restart");
                Button resume = createImageButton("resume");
                buttonBox.getChildren().addAll(exit, restart, resume);
            }

            case "win" -> {
                headerImage = createHeaderImage("congrats");
                Button exit = createImageButton("back");
                Button restart = createImageButton("restart");
                Button nextmode = createImageButton("nextmode");
                buttonBox.getChildren().addAll(exit, restart, nextmode);
            }
            case "lose" -> {
                headerImage = createHeaderImage("youlost");
                Button exit = createImageButton("back");
                Button restart = createImageButton("restart");
                buttonBox.getChildren().addAll(exit, restart);
            }
        }
        VBox.setMargin(buttonBox, new Insets(-120, 0, 0, 0));
        menuBox.getChildren().addAll(headerImage, buttonBox);
    }


    public Button createImageButton(String imagePath ) {
        double width = 160;
        double height = 160;
        Image image = new Image(GlobalSettings.getResource("graphics/Items/Buttons/" + imagePath + ".png").toString());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false);

        Button button = new Button();
        button.setGraphic(imageView);
        button.setPrefSize(width, height);
        button.setMinSize(width, height);
        button.setMaxSize(width, height);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        switch (imagePath){//logic
            case "restart" -> button.setOnAction(e-> System.out.println("restart"));
            case "back" -> button.setOnAction(e-> System.out.println("back"));
            case "resume" -> button.setOnAction(e-> System.out.println("resume"));
            case "nextlevel" -> button.setOnAction(e-> System.out.println("nextlevel"));
        }

        ColorAdjust hoverEffect = new ColorAdjust();
        hoverEffect.setBrightness(0.15);
        button.setOnMouseEntered(e -> button.setEffect(hoverEffect));
        button.setOnMouseExited(e -> button.setEffect(null));
        return button;
    }


    public ImageView createHeaderImage(String imageName) {
        String fullPath = GlobalSettings.getResource("graphics/Items/Messages/" + imageName + ".png").toString();
        Image image = new Image(fullPath);

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(550);
        imageView.setFitHeight(450);
        imageView.setPreserveRatio(true);

        VBox.setMargin(imageView, new Insets(-80, 0, 0, 0));
        return imageView;
    }
}

