package com.pvz.plantsvszombies.Presentation.Views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DayView extends AbstractLevelView {

    public static final double Width = 1280;
    public static final double Height = 728;

    private DayView() {
    }

    public static DayView createStage() {
        var dayView = new DayView();
        StackPane bottommostPlane = new StackPane();
        bottommostPlane.setBackground(new Background(
                new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)
        ));
        var scene = new Scene(bottommostPlane, Width, Height);
        dayView.gameBoxPane = bottommostPlane;
        dayView.setScene(scene);
        return dayView;

    }
}
