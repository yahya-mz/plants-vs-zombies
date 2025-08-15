package com.pvz.plantsvszombies.Presentation.GUI.Views;
import com.pvz.plantsvszombies.Domain.Common.GameMode;
import com.pvz.plantsvszombies.Domain.Engines.NightEngine;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundManager;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundType;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import com.pvz.plantsvszombies.Presentation.Engines.VisualNightEngine;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class PickingPlantStage {
    private final ArrayList<AbstractPlantGameObject.PlantType> selectedPlants = new ArrayList<>();
    private final HBox selectedPlantHBox = new HBox(0);
    private Stage primaryStage;
    private Button playBtn;
    private final GameMode _mode;
    private final String mode;
    private Image[] cardImages;

    public PickingPlantStage(GameMode mode){
        this._mode = mode;
    }

    private Image[] cardImages;

    public Stage createStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        VBox root = new VBox(5);
        StackPane mainPane = new StackPane();

        playBtn = createStartButton();
        StackPane.setAlignment(playBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(playBtn, new Insets(0, 0, -50, 0));
        playBtn.setDisable(true);

        root.setPadding(new Insets(20));

        Image bg = new Image(mode.equals("day")
                ? GlobalSettings.getResource("graphics/Items/Background/daypickingstage.png")
                : GlobalSettings.getResource("graphics/Items/Background/nightpickingstage.png"));
        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage bgImg = new BackgroundImage(bg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
        root.setBackground(new Background(bgImg));

        loadCardImages();

        ScrollPane cardScrollPane = createCardScrollPaneWithCards();
        int topMarginForScroll = mode.equals("day") ? 108 : 125;
        VBox.setMargin(cardScrollPane, new Insets(topMarginForScroll, 0, 0, 180));
        VBox.setMargin(selectedPlantHBox, new Insets(10, 0, 0, 125));

        root.getChildren().addAll(cardScrollPane, selectedPlantHBox);
        mainPane.getChildren().addAll(root, playBtn);

        Scene scene = new Scene(mainPane, 900, 600);
        Stage stage = new Stage();
        stage.setTitle("Picking Plant Stage");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        return stage;
    }

    private void loadCardImages() {
        File dir = new File(GlobalSettings.getDir("graphics/Cards"));
        File[] files = dir.listFiles();
        cardImages = new Image[files.length];
        for (int i = 0; i < cardImages.length; i++) {
            cardImages[i] = new Image(files[i].getPath());
        }
    }

    public ScrollPane createCardScrollPaneWithCards() {
        VBox cardVBox = new VBox(10);
        cardVBox.setPadding(new Insets(20, 0, 0, 0));
        cardVBox.setStyle("-fx-background-color: transparent;");
        cardVBox.setFillWidth(true);
        cardVBox.setBackground(Background.EMPTY);

        int maxHeight = mode.equals("day") ? 200 : 184;
        int maxWidth = 510;

        int perRow = 4;
        for (int i = 0; i < cardImages.length; i += perRow) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER);
            for (int j = i; j < i + perRow && j < cardImages.length; j++) {
                row.getChildren().add(createSelectableCard(j));
            }
            cardVBox.getChildren().add(row);
        }

        ScrollPane scrollPane = new ScrollPane(cardVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxHeight(maxHeight);
        scrollPane.setMaxWidth(maxWidth);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        String css = """
        .custom-scroll-pane .scroll-bar { -fx-background-color: transparent; }
        .custom-scroll-pane .scroll-bar:vertical .thumb { -fx-background-color: #4A2C00; -fx-background-insets: 2; -fx-background-radius: 5; }
        .custom-scroll-pane .scroll-bar:horizontal .thumb { -fx-background-color: #4A2C00; -fx-background-insets: 2; -fx-background-radius: 5; }
        .custom-scroll-pane .scroll-bar .track { -fx-background-color: transparent; }
        """;

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-background: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );

        Scene scene = scrollPane.getScene();
        if (scene != null) {
            scene.getStylesheets().add("data:text/css," + css);
        } else {
            scrollPane.sceneProperty().addListener((obs, o, n) -> {
                if (n != null) n.getStylesheets().add("data:text/css," + css);
            });
        }

        return scrollPane;
    }

    private ImageView createSelectableCard(int index) {
        ImageView card = new ImageView(cardImages[index]);
        card.setFitWidth(100);
        card.setPreserveRatio(true);
        card.setCursor(Cursor.HAND);
        card.setPickOnBounds(true);
        EffectsManagement.yAndScaleHoverEffectForNode(card);



        card.setOnMouseClicked(e -> {
            if (selectedPlants.size() < 6) {
                String imgPath = cardImages[index].getUrl();
                String imgName = imgPath.substring(imgPath.lastIndexOf('\\') + 1, imgPath.lastIndexOf('.'));
                AbstractPlantGameObject.PlantType type = AbstractPlantGameObject.PlantType.valueOf(imgName);

                selectedPlants.add(type);
                playBtn.setDisable(selectedPlants.size() != 6);

                ImageView cloned = new ImageView(cardImages[index]);
                cloned.setFitWidth(100);
                cloned.setPreserveRatio(true);
                cloned.setCursor(Cursor.HAND);
                cloned.setPickOnBounds(true);
                EffectsManagement.yAndScaleHoverEffectForNode(cloned);

                cloned.setOnMouseClicked(ev -> {
                    selectedPlantHBox.getChildren().remove(selectedPlants.indexOf(type));
                    selectedPlants.remove(type);
                    updateCardMargins();
                    card.setOpacity(1);
                    card.setDisable(false);
                    playBtn.setDisable(selectedPlants.size() != 6);
                });

                selectedPlantHBox.getChildren().add(cloned);
                updateCardMargins();

                card.setDisable(true);
                card.setOpacity(0.5);
            }
        });

        return card;
    }

    private static void hoverEffectForNode(Node node) {
        node.setOnMouseEntered(ev -> animateNode(node, -14, 1.1, 100));
        node.setOnMouseExited(ev -> animateNode(node, 0, 1.0, 120));
    }

    private static void animateNode(Node node, double toY, double scale, int ms) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), node);
        tt.setToY(toY);
        tt.playFromStart();

        ScaleTransition st = new ScaleTransition(Duration.millis(ms), node);
        st.setToX(scale);
        st.setToY(scale);
        st.playFromStart();
    }

    private void updateCardMargins() {
        for (int i = 0; i < selectedPlantHBox.getChildren().size(); i++) {
            HBox.setMargin(selectedPlantHBox.getChildren().get(i), switch (i) {
                case 0 -> new Insets(0, 2, 0, 0);
                case 1 -> new Insets(0, 1, 0, 0);
                default -> new Insets(0, 0, 0, 0);
            });
        }
    }
    public Button createStartButton() {
        double w = 250, h = 250;
        String[] paths = {
                "graphics/Items/Buttons/play/normalplay.png",
                "graphics/Items/Buttons/play/onactionplay.png"
        };

        Image normal = new Image(GlobalSettings.getResource(paths[0]));
        Image hover  = new Image(GlobalSettings.getResource(paths[1]));

        ImageView iv = new ImageView(normal);
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(true);

        Button btn = new Button();
        // مهم: اندازه‌ی دکمه را به اندازه‌ی گرافیک بسپار
        // این دو خط را حذف کن: btn.setMinSize(w, h); btn.setMaxSize(w, h);
        btn.setGraphic(iv);
        btn.setBackground(Background.EMPTY);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 0;");
        btn.setPickOnBounds(false); // فقط وقتی اشاره‌گر واقعاً روی بدنه‌ی دکمه/گرافیکه، رخداد بگیره

        // Hover فقط وقتی روی خود تصویر هستیم
        btn.setOnMouseEntered(e -> iv.setImage(hover));
        btn.setOnMouseExited(e -> iv.setImage(normal));

        btn.setOnAction(e -> {
            Stage gameStage = mode.equals("day")
                    ? DayView.createStage(selectedPlants)
                    : NightView.createStage(selectedPlants);

            if (mode.equals("day")) SoundManager.play(SoundType.DAY_BACKGROUND);
            else SoundManager.play(SoundType.NIGHT_BACKGROUND);

            gameStage.show();
            gameStage.setOnHiding(ev -> {
                primaryStage.show();
                Mediator.getInstance().stopGameEngine();
                SoundManager.stopAll();
            });
            ((Stage) playBtn.getScene().getWindow()).close();
        });

        return btn;
    }
}
