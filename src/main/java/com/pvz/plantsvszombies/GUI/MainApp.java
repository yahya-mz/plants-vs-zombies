package com.pvz.plantsvszombies.GUI;

import com.pvz.plantsvszombies.GameEngine.DayEngine;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Views.DayView;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.Duration;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch();
    }

    private boolean engineThreadRunning = false;

    @Override
    public void start(Stage primaryStage) {
        var root = new StackPane();
        root.setBackground(new Background(
                new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)
        ));
        var btn = new Button("Start Day");

        btn.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                var gameStage = DayView.createStage();
                DayEngine dayEngine = new DayEngine(DayView.Width, DayView.Height);
                VisualEngine.init(gameStage);
                primaryStage.close();
                gameStage.show();
                dayEngine.start();
                engineThreadRunning = true;
                var gameEngineThread = new Thread(() -> {
                    while (engineThreadRunning) {
                        try {
                            dayEngine.update();
                            Thread.sleep(Duration.ofMillis(1000 / GlobalSettings.FPS));
                        } catch (Exception ex) {

                        }
                    }
                });
                gameStage.setOnHiding((event) -> {
                    System.out.println("Stopping GameEngine");
                    engineThreadRunning = false;
                });
                gameEngineThread.start();
            }
        });

        root.getChildren().add(btn);
        Scene scene = new Scene(root, 320, 240);
        primaryStage.setTitle("Hello!");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
