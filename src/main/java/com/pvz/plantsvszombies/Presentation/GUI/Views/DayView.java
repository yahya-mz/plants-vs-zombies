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
package com.pvz.plantsvszombies.Presentation.GUI.Views;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Engines.DayEngine;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.*;
import com.pvz.plantsvszombies.Presentation.Engines.VisualDayEngine;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;

public class DayView extends AbstractLevelView {

    public static final double Width = 1200;
    public static final double Height = 728;
    private static IntegerProperty counterValue = new SimpleIntegerProperty(0);
    private VisualDayEngine _visualEngine;
    private static StackPane bottommostPlane;


    private DayView() {
    }

    public static DayView createStage(List<AbstractPlantGameObject.PlantType> selectedPlants) {
        bottommostPlane = new StackPane();//changed
        var dayView = new DayView();
        dayView.setupEngines();
        dayView._gameBoxPane = bottommostPlane;

        //ready-set-plant
        VBox animationBox = new VBox();
        animationBox.setPrefSize(500, 500);
        animationBox.setAlignment(Pos.CENTER);
        bottommostPlane.getChildren().add(animationBox);
        StackPane.setAlignment(animationBox, Pos.CENTER);
        playImageSequenceInBox(animationBox, () -> {
            bottommostPlane.getChildren().remove(animationBox);
        });
        //

        //bar
        VBox suncounter = createCounter();
        HBox plantbar = dayView.createTopPlantSelectionBar(selectedPlants);
        addHoverEffectToImages(plantbar);
        Button shuveltool = createShovelToolButton();
        Button pause = createPauseButton();

        HBox mainbar = new HBox(80);
//        mainbar.setStyle("-fx-background-color: red;");-debug
        mainbar.setMaxWidth(1100);
        mainbar.setMaxHeight(100);
        mainbar.getChildren().addAll(suncounter, plantbar, shuveltool);
        mainbar.setAlignment(Pos.TOP_CENTER);
        mainbar.setMouseTransparent(false);
        mainbar.setPickOnBounds(true);
        HBox.setMargin(suncounter, new Insets(73, 60, 0, 220));//dont change this
        HBox.setMargin(plantbar, new Insets(0,85,0, -70));
        mainbar.setMinWidth(50);
        mainbar.setMinHeight(30);

        bottommostPlane.getChildren().addAll(mainbar , pause);
        //StackPane.setMargin(mainbar, new Insets(20, -40, 200, 60));//dont change this
        bottommostPlane.setAlignment(pause, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(mainbar, Pos.TOP_CENTER);
        StackPane.setMargin(mainbar, new Insets(15, 60, 185,-20));


        var scene = new Scene(bottommostPlane, Width, Height);


        dayView.setScene(scene);
        dayView.setResizable(false);


        return dayView;

    }

    private void setupEngines() {
        DayEngine dayEngine = new DayEngine(DayView.Width, DayView.Height);
        _visualEngine = new VisualDayEngine(this, dayEngine);
        Mediator.init(dayEngine, _visualEngine);
        Mediator.getInstance().startGameEngine();

        this.setOnHiding((event) -> {
            System.out.println("Stopping GameEngine");
            Mediator.getInstance().stopGameEngine();
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

    public HBox createTopPlantSelectionBar(List<AbstractPlantGameObject.PlantType> selectedPlants) {
        HBox buttonBar = new HBox(0);

        buttonBar.setAlignment(Pos.TOP_CENTER);
        buttonBar.setPrefHeight(86);

        for (int i = 0; i < selectedPlants.size(); i++) {
            Button btn = new Button("Item " + i);
            btn.setCursor(Cursor.HAND);
            Image path = new Image(GlobalSettings.getResource("graphics/Cards/Day/" + selectedPlants.get(i).name() + ".png"), true);
            ImageView cardpic = new ImageView(path);

            cardpic.setPreserveRatio(true);
            cardpic.setSmooth(true);
            cardpic.setFitWidth(79);

            Insets margin;
            switch (i) {
                case 0 -> margin = new Insets(5, 4, 0, 40);
                case 1 -> margin = new Insets(5, 0, 0, 0);
                case 2 -> margin = new Insets(5, 3, 0, 5);
                case 3 -> margin = new Insets(5, 25, 0, 0);
                case 4 -> margin = new Insets(5, 4, 0, -20);
                case 5 -> margin = new Insets(5, 8, 0, 0);
                default -> margin = new Insets(5, 0, 0, 0);
            }
            HBox.setMargin(btn, margin);


            final AbstractPlantGameObject.PlantType final_plantType = selectedPlants.get(i);
            btn.setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    switch (final_plantType) {
                        case PEASHOOTER -> _visualEngine.setSelectedPlantType(PeashooterVisualObject.class);
                        case SUNFLOWER -> _visualEngine.setSelectedPlantType(SunFlowerVisualObject.class);
                        case WALL_NUT -> _visualEngine.setSelectedPlantType(WallNutVisualObject.class);
                        case JALAPENO -> _visualEngine.setSelectedPlantType(JalapenoVisualObject.class);
                        case TALL_NUT -> _visualEngine.setSelectedPlantType(TallnutVisualObject.class);
                        case CHERRY_BOMB -> _visualEngine.setSelectedPlantType(CherryBombVisualObject.class);
                        case SNOW_PEA -> _visualEngine.setSelectedPlantType(SnowPeaVisualObject.class);
                        case REPEATER -> _visualEngine.setSelectedPlantType(RepeaterVisualObject.class);
                    }
                }
            });
//                                    if (e.getButton().equals(MouseButton.PRIMARY)) {
//                            this._visualEngine.plant(PeashooterVisualObject.class
//                                , 4, 5);


            btn.setGraphic(cardpic);
            btn.setPrefSize(70, 90);
            btn.setMinSize(70, 90);
            btn.setMaxSize(70, 90);
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");

            buttonBar.getChildren().add(btn);
        }

        return buttonBar;
    }

    public static void addHoverEffectToImages(HBox hbox) {//fixing
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

    public static Button createShovelToolButton() {
        Button shovelButton = new Button("Shovel");
        Image path = new Image(GlobalSettings.getResource("graphics/Items/Buttons/shuveltool.png"), true);
        ImageView shuvelPic = new ImageView(path);
        shuvelPic.setPreserveRatio(false);
        shovelButton.setGraphic(shuvelPic);

        shovelButton.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");
        shuvelPic.setFitHeight(50);
        shuvelPic.setFitWidth(50);
//        shovelButton.setMaxHeight(20);
//        shovelButton.setMaxWidth(20);
        shovelButton.setOnAction(e -> {
            System.out.println("Shovel tool activated!");

        });

        return shovelButton;
    }

    public static Button createPauseButton() {
        Button PauseButton = new Button();

        Image path = new Image(GlobalSettings.getResource("graphics/Items/Buttons/pause.png"), true);
        ImageView pauseImage = new ImageView(path);

        pauseImage.setPreserveRatio(true);
        pauseImage.fitHeightProperty().bind(PauseButton.heightProperty());

        PauseButton.setPrefSize(90, 90);
        PauseButton.setMinSize(90, 90);
        PauseButton.setMaxSize(90, 90);

        PauseButton.setGraphic(pauseImage);
        PauseButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        PauseButton.setOnAction(e -> {
            DayMenu.createPausePopup().show();
            System.out.println("Pause button clicked");
        });

        return PauseButton;
    }





    public static void playImageSequenceInBox(Pane targetBox, Runnable onFinished) {
        String[] imagePaths = {
                "graphics/Items/Messages/ready.png",
                "graphics/Items/Messages/set.png",
                "graphics/Items/Messages/plant.png"
        };

        ImageView imageView = new ImageView();
        imageView.setFitWidth(800);
        imageView.setFitHeight(800);
        imageView.setPreserveRatio(true);

        targetBox.getChildren().clear();
        targetBox.getChildren().add(imageView);

        SequentialTransition sequence = new SequentialTransition();

        for (int i = 0; i < imagePaths.length; i++) {
            String path = imagePaths[i];
            Image image = new Image(GlobalSettings.getResource(path).toString(), true);
            PauseTransition setImage = new PauseTransition(Duration.ZERO);
            setImage.setOnFinished(e -> imageView.setImage(image));

            FadeTransition fadeIn;
            if (i==0){
                fadeIn = new FadeTransition(Duration.seconds(1.2), imageView);

            }else {
                fadeIn = new FadeTransition(Duration.seconds(0.5), imageView);
            }
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), imageView);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            Duration stayDuration;
            if (i == 0 || i == 1) {
                stayDuration = Duration.seconds(0.2);
            } else {
                stayDuration = Duration.seconds(0.8);
            }
            FadeTransition stay = new FadeTransition(stayDuration, imageView);
            stay.setFromValue(1);
            stay.setToValue(1);

            sequence.getChildren().addAll(setImage, fadeIn, stay, fadeOut);
        }

        sequence.setOnFinished(e -> {
            targetBox.getChildren().clear();
            onFinished.run();
        });

        sequence.play();
    }


    public void showWave1(){
        showWaveImages(bottommostPlane , "wave1");
    }
    public void showWave2(){
        showWaveImages(bottommostPlane , "wave2");
    }
    public void showWave3(){
        showWaveImages(bottommostPlane , "wave3");
    }
    public void showFinalWave(){
        showWaveImages(bottommostPlane , "finalwave");
    }
    private static void showWaveImages(StackPane parentPane, String imageName) {

        VBox overlayBox = new VBox();
        overlayBox.setAlignment(Pos.CENTER);
        overlayBox.setPrefSize(parentPane.getWidth(), parentPane.getHeight());


        ImageView imageView = new ImageView();
        imageView.setFitWidth(800);
        imageView.setFitHeight(800);
        imageView.setPreserveRatio(true);
        String path = "graphics/Items/Messages/" + imageName + ".png";
        Image image = new Image(GlobalSettings.getResource(path).toString(), true);
        imageView.setImage(image);

        overlayBox.getChildren().add(imageView);
        parentPane.getChildren().add(overlayBox);
        StackPane.setAlignment(overlayBox, Pos.CENTER);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), imageView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition stay = new FadeTransition(Duration.seconds(1.0), imageView);
        stay.setFromValue(1);
        stay.setToValue(1);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), imageView);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);
        sequence.setOnFinished(e -> parentPane.getChildren().remove(overlayBox));
        sequence.play();
    }
}

