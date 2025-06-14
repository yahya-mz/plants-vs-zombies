package com.pvz.plantsvszombies.GUI.Views;

import com.pvz.plantsvszombies.GameEngine.DayEngine;
import com.pvz.plantsvszombies.Mediator.Mediator;
import com.pvz.plantsvszombies.Presentation.Entities.PeashooterVisualObject;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DayView extends AbstractLevelView {

    public static final double Width = 1280;
    public static final double Height = 728;

    private boolean engineThreadRunning = false;

    private VisualEngine _visualEngine;

    private DayView() {
    }

    public static DayView createStage() {
        var dayView = new DayView();
        dayView.setupEngines();
        StackPane bottommostPlane = new StackPane();
        bottommostPlane.setBackground(new Background(
                new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        var btn = new Button("Spawn Peashooter");
        bottommostPlane.getChildren().add(btn);
        btn.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                dayView._visualEngine.plant(PeashooterVisualObject.class
                        , 1, 1);
            }
        });

        var btn2 = new Button("Spawn Peashooter 2");
        bottommostPlane.getChildren().add(btn2);
        btn2.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                dayView._visualEngine.plant(PeashooterVisualObject.class
                        , 2, 2);
            }
        });


        var scene = new Scene(bottommostPlane, Width, Height);
        dayView._gameBoxPane = bottommostPlane;
        dayView.setScene(scene);
        return dayView;

    }


    private void setupEngines() {
        DayEngine dayEngine = new DayEngine(DayView.Width, DayView.Height);
        _visualEngine = new VisualEngine(this, dayEngine);
        Mediator.init(dayEngine, _visualEngine);
        Mediator.getInstance().startEngine();

        this.setOnHiding((event) -> {
            System.out.println("Stopping GameEngine");
            Mediator.getInstance().stopEngine();
        });
    }
}
