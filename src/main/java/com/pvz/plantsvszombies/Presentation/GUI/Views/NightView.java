package com.pvz.plantsvszombies.Presentation.GUI.Views;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Engines.NightEngine;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.*;
import com.pvz.plantsvszombies.Presentation.Engines.VisualNightEngine;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.List;

public class NightView extends AbstractLevelView {

    public static final double Width = 1200;
    public static final double Height = 728;
    public int cursorType;
    private static final IntegerProperty counterValue = new SimpleIntegerProperty(0);
    private VisualNightEngine _visualEngine;
    private static StackPane bottommostPlane;
    private static final BooleanProperty isShovelMode = new SimpleBooleanProperty(false);
    private static Button shovelButton;



    private NightView() {
    }

    public static NightView createStage(List<AbstractPlantGameObject.PlantType> selectedPlants) {
        var NightView = new NightView();
        bottommostPlane = new StackPane();//changed
        NightView._gameBoxPane = bottommostPlane;

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
        HBox plantbar = NightView.createTopPlantSelectionBar(selectedPlants);
        addHoverEffectToImages(plantbar);
        Button shoveltool = NightView.createShovelToolButton();
        Button pause = createPauseButton();

        HBox mainbar = new HBox(80);
//        mainbar.setStyle("-fx-background-color: red;");-debug
        mainbar.setMaxWidth(1100);
        mainbar.setMaxHeight(100);
        mainbar.getChildren().addAll(suncounter, plantbar, shoveltool);
        mainbar.setAlignment(Pos.TOP_CENTER);
        mainbar.setMouseTransparent(false);
        mainbar.setPickOnBounds(true);
        HBox.setMargin(suncounter, new Insets(73, 60, 0, 236));//dont change this
        HBox.setMargin(plantbar, new Insets(0,85,0, -70));
        HBox.setMargin(shoveltool , new Insets(-14,0,0,-30));
        mainbar.setMinWidth(50);
        mainbar.setMinHeight(30);

        bottommostPlane.getChildren().addAll(mainbar , pause);
        //StackPane.setMargin(mainbar, new Insets(20, -40, 200, 60));//dont change this
        StackPane.setAlignment(pause, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(mainbar, Pos.TOP_CENTER);
        StackPane.setMargin(mainbar, new Insets(15, 60, 185,-20));


        var scene = new Scene(bottommostPlane, Width, Height);


        NightView.setScene(scene);
        NightView.setResizable(false);
        NightView.setupEngines();

        return NightView;

    }

    private void setupEngines() {
        NightEngine NightEngine = new NightEngine(NightView.Width, NightView.Height);
        _visualEngine = new VisualNightEngine(this, NightEngine);
        Mediator.init(NightEngine, _visualEngine);
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
            Image path = new Image(GlobalSettings.getResource("graphics/Cards/" + selectedPlants.get(i).name() + ".png"), true);
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
                        case PUFF_SHROOM -> _visualEngine.setSelectedPlantType(ScaredyshroomVisualObject.class);
                        case SCAREDY_SHROOM -> _visualEngine.setSelectedPlantType(PuffshroomVisualObject.class);
                        case ICE_SHROOM -> _visualEngine.setSelectedPlantType(IceshroomVisualObject.class);
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
            if (node instanceof ImageView imageView) {

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

    public Button createShovelToolButton() {
        Image shovelCursorImage = new Image(GlobalSettings.getResource("graphics/Items/bar/shoveltool/shovel.png"));
        Image shovelFullImage = new Image(GlobalSettings.getResource("graphics/Items/bar/shoveltool/shovelToolFull.png"), true);
        Image shovelEmptyImage = new Image(GlobalSettings.getResource("graphics/Items/bar/shoveltool/shovelToolEmpty.png"), true);

        Button shovelButton = new Button();
        ImageView shovelFullView = new ImageView(shovelFullImage);
        ImageView shovelEmptyView = new ImageView(shovelEmptyImage);

        shovelBarView(shovelButton, shovelFullView);

        // ✅ فقط یک‌بار لیسنر اضافه می‌کنیم
        NightView.isShovelModeProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && shovelButton.getScene() != null) {
                Platform.runLater(() -> {
                    shovelButton.getScene().setCursor(Cursor.DEFAULT);
                    shovelBarView(shovelButton, new ImageView(shovelFullImage));
                });
            }
        });

        shovelButton.setOnAction(e -> {
            NightView.setIsShovelMode(true); // ✅ به‌جای isShovelMode = true;

            Scene scene = shovelButton.getScene();
            if (scene != null && NightView.getIsShovelMode()) {
                scene.setCursor(new ImageCursor(shovelCursorImage, 32, 32));
                _visualEngine.shovelActivation();
                shovelBarView(shovelButton, new ImageView(shovelEmptyImage));

                scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY && NightView.getIsShovelMode()) {
                        NightView.setIsShovelMode(false);
                    }
                });
            }
        });

        return shovelButton;
    }

    public static BooleanProperty isShovelModeProperty() {
        return isShovelMode;
    }

    public static void setIsShovelMode(boolean value) {
        isShovelMode.set(value);
    }

    public static boolean getIsShovelMode() {
        return isShovelMode.get();
    }

    private static void shovelBarView(Button btn , ImageView shovelView){
        shovelView.setFitWidth(120);
        shovelView.setFitHeight(120);
        shovelView.setPreserveRatio(false);
        btn.setGraphic(shovelView);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");
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
            Image image = new Image(GlobalSettings.getResource(path), true);
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
        Image image = new Image(GlobalSettings.getResource(path), true);
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

