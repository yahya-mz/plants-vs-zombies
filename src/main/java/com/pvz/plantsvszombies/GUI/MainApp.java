//}
package com.pvz.plantsvszombies.GUI;

import com.pvz.plantsvszombies.GameEngine.DayEngine;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.GUI.Views.DayView;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.Duration;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private boolean engineThreadRunning = false;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        Image backgroundImage = new Image(GlobalSettings.getResource("graphics/Items/Background/daymenu.jpg").toString());
        ImageView bgImageView = new ImageView(backgroundImage);

        bgImageView.setPreserveRatio(true);
        bgImageView.setFitWidth(800);
        bgImageView.setFitHeight(533);

        root.getChildren().add(bgImageView);

        Button dayModeBtn = createHoverButton(
                "graphics/Items/Buttons/day.png",
                "graphics/Items/Buttons/day2.png",
                250, 100
        );

        Button nightModeBtn = createHoverButton(
                "graphics/Items/Buttons/night.png",
                "graphics/Items/Buttons/night2.png",
                250, 100
        );

        dayModeBtn.setOnAction(e -> {
            launchGame(primaryStage);
        });

        nightModeBtn.setOnAction(e -> {
            // Add night mode functionality here
        });


        VBox buttonContainer = new VBox(-30, dayModeBtn, nightModeBtn);//VBox for mode btn
        buttonContainer.setAlignment(Pos.CENTER);

        root.getChildren().add(buttonContainer);
        StackPane.setAlignment(buttonContainer, Pos.CENTER);


        Scene scene = new Scene(root, 800, 533);
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


    private void launchGame(Stage primaryStage) {//for launching game

        try {
            DayView gameStage = DayView.createStage();
            DayEngine dayEngine = new DayEngine(DayView.Width, DayView.Height);//they are static
//            VisualEngine.init(gameStage);

            primaryStage.hide();

            gameStage.show();

            engineThreadRunning = true;
            Thread gameEngineThread = new Thread(() -> {
                while (engineThreadRunning) {
                    try {
                        dayEngine.update();//every obj for doing a function
                        Thread.sleep(Duration.ofMillis(1000 / GlobalSettings.FPS));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            gameStage.setOnHiding(e -> {
                engineThreadRunning = false;
                primaryStage.show(); // Show main menu again
            });

            gameEngineThread.setDaemon(true);
            gameEngineThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            primaryStage.show();
        }
    }
}
