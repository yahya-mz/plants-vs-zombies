//package com.pvz.plantsvszombies.GUI.Views;
//
//import com.pvz.plantsvszombies.GameEngine.DayEngine;
//import com.pvz.plantsvszombies.Mediator.Mediator;
//import com.pvz.plantsvszombies.Presentation.Entities.Plants.PeashooterVisualObject;
//import com.pvz.plantsvszombies.Presentation.VisualEngine;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//
//public class DayView extends AbstractLevelView {
//
//    public static final double Width = 1280;
//    public static final double Height = 728;
//
//    private boolean engineThreadRunning = false;
//
//    private VisualEngine _visualEngine;
//
//    private DayView() {
//    }
//
//    public static DayView createStage() {
//        var dayView = new DayView();
//        dayView.setupEngines();
//        StackPane bottommostPlane = new StackPane();
//        bottommostPlane.setBackground(new Background(
//                new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)
//        ));
//
//        var btn = new Button("Spawn Peashooter");
//        bottommostPlane.getChildren().add(btn);
//        btn.setOnMouseClicked((e) -> {
//            if (e.getButton().equals(MouseButton.PRIMARY)) {
//                dayView._visualEngine.plant(PeashooterVisualObject.class
//                        , 1, 1);
//            }
//        });
//
//        var btn2 = new Button("Spawn Peashooter 2");
//        bottommostPlane.getChildren().add(btn2);
//        btn2.setOnMouseClicked((e) -> {
//            if (e.getButton().equals(MouseButton.PRIMARY)) {
//                dayView._visualEngine.plant(PeashooterVisualObject.class
//                        , 2, 2);
//            }
//        });
//
//
//        var scene = new Scene(bottommostPlane, Width, Height);
//        dayView._gameBoxPane = bottommostPlane;
//        dayView.setScene(scene);
//        return dayView;
//
//    }
//
//
//    private void setupEngines() {
//        DayEngine dayEngine = new DayEngine(DayView.Width, DayView.Height);
//        _visualEngine = new VisualEngine(this, dayEngine);
//        Mediator.init(dayEngine, _visualEngine);
//        Mediator.getInstance().startEngine();
//
//        this.setOnHiding((event) -> {
//            System.out.println("Stopping GameEngine");
//            Mediator.getInstance().stopEngine();
//        });
//    }
//}
package com.pvz.plantsvszombies.GUI.Views;

import com.pvz.plantsvszombies.GameEngine.DayEngine;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.PeashooterVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.RepeaterVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.WallNutVisualObject;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

public class DayView extends AbstractLevelView {

    public static final double Width = 1088;
    public static final double Height = 728;
    private static IntegerProperty counterValue = new SimpleIntegerProperty(0);
    private VisualEngine _visualEngine;

    private DayView() {
    }

    public static DayView createStage() {
        var dayView = new DayView();
        dayView.setupEngines();
        StackPane bottommostPlane = new StackPane();
        dayView._gameBoxPane = bottommostPlane;

        bottommostPlane.setPrefSize(100,200);

//        bottommostPlane.setBackground(new Background(
//                new BackgroundImage(
//                        new Image(GlobalSettings.getResource("graphics/Items/Background/daymap.jpg").toString(), true),
//                        BackgroundRepeat.NO_REPEAT,
//                        BackgroundRepeat.NO_REPEAT,
//                        BackgroundPosition.CENTER,
//                        new BackgroundSize(
//                                100, 100,
//                                true, true,
//                                false, true
//                        )
//
//                )
//        ));

        //bar
        VBox suncounter = createCounter();
        HBox plantbar = dayView.createButtonBar();
        Button shuveltool = createShovelToolButton();

        HBox mainbar = new HBox(24);
//        mainbar.setStyle("-fx-background-color: red;");-debug
        mainbar.setMaxWidth(850);
        mainbar.setMaxHeight(100);

        mainbar.getChildren().addAll(suncounter, plantbar, shuveltool);
        mainbar.setAlignment(Pos.TOP_CENTER);

        mainbar.setMouseTransparent(false);
        mainbar.setPickOnBounds(true);


        HBox.setMargin(suncounter, new Insets(64, 34, 0, -20));//dont change this
        mainbar.setMinWidth(50);
        mainbar.setMinHeight(30);


        bottommostPlane.getChildren().addAll(mainbar);

        //StackPane.setMargin(mainbar, new Insets(20, -40, 200, 60));//dont change this
        StackPane.setAlignment(mainbar, Pos.TOP_CENTER);
        StackPane.setMargin(mainbar, new Insets(20, -40, 200, 65));


        var scene = new Scene(bottommostPlane, Width, Height);


        dayView.setScene(scene);
        dayView.setResizable(false);

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


    public static VBox createCounter() {
        Label counterLabel = new Label();
        counterLabel.textProperty().bind(counterValue.asString());
        counterLabel.setId("counter-label");

        VBox counterBox = new VBox(counterLabel);
        counterBox.setAlignment(Pos.TOP_CENTER);
        counterBox.setPrefHeight(100);
        counterBox.setMinWidth(50);

        return counterBox;
    }

    public int setMyCounterValue() {//updating counterval
        counterValue.set(0);
        return counterValue.get(); //mainenginevalue
    }

    public HBox createButtonBar() {


        HBox buttonBar = new HBox(3);
        buttonBar.setAlignment(Pos.TOP_CENTER);
        buttonBar.setPrefHeight(90);

        for (int i = 1; i <= 8; i++) {
            Button btn = new Button("Item " + i);
            btn.setCursor(Cursor.HAND);
            Image path = new Image(GlobalSettings.getResource("graphics/Cards/" + i + ".png").toString(), true);
            ImageView cardpic = new ImageView(path);

            cardpic.setPreserveRatio(true);
            cardpic.setSmooth(true);
            cardpic.setFitWidth(70);


            final int plantType = i;
            btn.setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    switch (plantType) {
                        case 1 -> _visualEngine.setSelectedPlantType(PeashooterVisualObject.class);
                        case 3 -> _visualEngine.setSelectedPlantType(WallNutVisualObject.class);
                        // سایر case ها برای گیاه‌های دیگه
                        //            if (e.getButton().equals(MouseButton.PRIMARY)) {
//                            this._visualEngine.plant(PeashooterVisualObject.class
//                                , 4, 5);
                        case 8 -> _visualEngine.setSelectedPlantType(RepeaterVisualObject.class);
                    }
                }
            });



            btn.setGraphic(cardpic);
            btn.setPrefSize(70, 90);
            btn.setMinSize(70, 90);
            btn.setMaxSize(70, 90);
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");


            btn.setOnAction(e -> {
                System.out.println("Plant button " + plantType + " clicked");
            });

            buttonBar.getChildren().add(btn);
        }

        return buttonBar;
    }



    public static Button createShovelToolButton() {
        Button shovelButton = new Button("Shovel");
        shovelButton.setPrefWidth(90);
        shovelButton.setPrefHeight(90);
        shovelButton.setStyle("-fx-background-color: brown; -fx-text-fill: white; -fx-font-weight: bold;");
        shovelButton.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");
        shovelButton.setOnAction(e -> {
            System.out.println("Shovel tool activated!");

        });

        return shovelButton;
    }





}

