//}
package com.pvz.plantsvszombies.Presentation.GUI;

import com.pvz.plantsvszombies.Presentation.GUI.Views.PickingPlantStage;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundManager;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        System.out.println("MainApp.main() called");
        SoundManager.initialize();
        System.out.println("SoundManager initialized, launching application...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        Image daybackgroundImage = new Image(GlobalSettings.getResource("graphics/Items/Background/daymenu.jpg"));
        Image nightbackgroundImage = new Image(GlobalSettings.getResource("graphics/Items/Background/nightmenu.jpg"));

        ImageView daybgImageView = new ImageView(daybackgroundImage);
        ImageView nightbgImageView = new ImageView(nightbackgroundImage);

        daybgImageView.setPreserveRatio(true);
        daybgImageView.setFitWidth(800);
        daybgImageView.setFitHeight(533);

        nightbgImageView.setPreserveRatio(true);
        nightbgImageView.setFitWidth(800);
        nightbgImageView.setFitHeight(533);
        nightbgImageView.setVisible(false);

        root.getChildren().addAll(daybgImageView, nightbgImageView);


        Button dayModeBtn = createHoverButton(
                "graphics/Items/Buttons/day.png",
                "graphics/Items/Buttons/day2.png",
                250, 100
        );

        dayModeBtn.setOnAction(e -> {
            launchGame(primaryStage, "day");
        });

        Button nightModeBtn = createHoverButton(
                "graphics/Items/Buttons/night.png",
                "graphics/Items/Buttons/night2.png",
                250, 100
        );
        nightModeBtn.setOnMouseEntered(e -> {
            daybgImageView.setVisible(false);
            nightbgImageView.setVisible(true);
        });
        nightModeBtn.setOnMouseExited(e -> {
            nightbgImageView.setVisible(false);
            daybgImageView.setVisible(true);
        });

        nightModeBtn.setOnAction(e -> {
            launchGame(primaryStage, "night");
        });


        VBox buttonContainer = new VBox(-30, dayModeBtn, nightModeBtn);//VBox for mode btn
        buttonContainer.setAlignment(Pos.CENTER);

        root.getChildren().add(buttonContainer);
        StackPane.setAlignment(buttonContainer, Pos.CENTER);


        Scene scene = new Scene(root, 799, 532);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Plants vs Zombies");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    private Button createHoverButton(String normalImagePath, String hoverImagePath, double width, double height) {
        Button button = new Button();

        Image normalImage = new Image(GlobalSettings.getResource(normalImagePath).toString());
        Image hoverImage = new Image(GlobalSettings.getResource(hoverImagePath).toString());
        ImageView normalView = new ImageView(normalImage);
        ImageView hoverView = new ImageView(hoverImage);

        normalView.setFitWidth(width);
        normalView.setFitHeight(height);
        normalView.setPreserveRatio(true);
        hoverView.setFitWidth(width);
        hoverView.setFitHeight(height);
        hoverView.setPreserveRatio(true);

        button.setGraphic(normalView);
        button.setCursor(javafx.scene.Cursor.HAND);

        // حذف حاشیه، رنگ پس‌زمینه و هرگونه padding
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");

        // Hover effects
        button.setOnMouseEntered(e -> button.setGraphic(hoverView));
        button.setOnMouseExited(e -> button.setGraphic(normalView));

        return button;
    }


        private void launchGame(Stage primaryStage, String mode) {//for launching game
            try {
                PickingPlantStage pickingStageBuilder = new PickingPlantStage(mode);
                Stage pickingPlantStage = pickingStageBuilder.createStage(primaryStage);
                pickingPlantStage.show();
                pickingPlantStage.setOnCloseRequest(e -> primaryStage.show());
                primaryStage.hide();

            } catch (Exception e) {
                e.printStackTrace();
                primaryStage.show();
            }
        }
    }
