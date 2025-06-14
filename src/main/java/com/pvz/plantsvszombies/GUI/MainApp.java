package com.pvz.plantsvszombies.GUI;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GUI.Views.DayView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.UUID;

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
                primaryStage.close();
                gameStage.show();
            }
        });

        root.getChildren().add(btn);
        Scene scene = new Scene(root, 320, 240);
        primaryStage.setTitle("Hello!");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    void demo(IGameEngine engine) throws Exception {
        // Demo:
        String PeashooterObjectId = "Peashooter" + UUID.randomUUID();
        Coordinate coordinate2 = new Coordinate(-1280.0 / 2, 728.0 / 2);
        var obj = PeashooterGameObject.createPeashooterGameObject(engine, PeashooterObjectId, coordinate2, 1, 1);
        engine.plantObject(obj);


        String PeashooterObjectId2 = "Peashooter" + UUID.randomUUID();
        Coordinate coordinate3 = new Coordinate(-1280.0 / 2, 728.0 / 2);
        var obj2 = PeashooterGameObject.createPeashooterGameObject(engine, PeashooterObjectId2, coordinate3, 2, 2);
        engine.plantObject(obj2);
    }
}
