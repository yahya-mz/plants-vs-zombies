package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.RepeaterGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.WallNutGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Entities.Bullets.NormalBulletVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.PeashooterVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.RepeaterVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.WallNutVisualObject;
import com.pvz.plantsvszombies.Presentation.Engines.VisualDayEngine;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import  com.pvz.plantsvszombies.Domain.Entities.Plants.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.SunFlowerGameObject;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.*;
import javafx.animation.FadeTransition;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import javafx.util.Duration;
import java.util.List;

public class MapVisualObject extends AbstractVisualObject {
    MapGameObject _mapObject;
    private final VisualDayEngine _engine;
    private final StackPane[][] visualGrid = new StackPane[5][9];

    public MapVisualObject(MapGameObject object, VisualDayEngine engine) {
        this._mapObject = object;
        this._engine = engine;

        VBox mainContainer = new VBox();
        mainContainer.setSpacing(15);
        mainContainer.setPadding(new Insets(140, 70, 0, 190));


        mainContainer.setBackground(new Background(
                new BackgroundImage(
                        new Image(GlobalSettings.getResource("graphics/Items/Background/daymap.jpg").toString(), true),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(100, 100, true, true, false, true)
                )
        ));

        //
        for (int row = 0; row < 5; row++) {
            HBox rowBox = new HBox();

            double customHGap = switch (row) {//فاصله بین هر عنصر در یک ریدف
                case 0 -> 4;
                case 1 -> 4;
                case 2 -> 4;
                case 3 -> 4;
                case 4 -> 4;
                default -> 7;
            };


            double buttonWidth = MapBlock.BLOCK_SIZE, buttonHeight = MapBlock.BLOCK_SIZE;


            Insets rowMargin = switch (row) {//جابه جایی هر ردیف
                case 0 -> new Insets(-5, -10, -10, -10);
                case 1 -> new Insets(5, -10, -10, -15);//up,right,down,left
                case 2 -> new Insets(7, -10, -10, -20);
                case 3 -> new Insets(7, -10, -10, -40);
                case 4 -> new Insets(7, -10, -10, -52);
                default -> Insets.EMPTY;
            };
//            VBox.setMargin(rowBox, rowMargin);
//            rowBox.setSpacing(customHGap);
            rowBox.setAlignment(Pos.CENTER_LEFT);

            for (int col = 0; col < 9; col++) {
                Button cellButton = new Button();
                cellButton.setPrefSize(buttonWidth, buttonHeight);
                cellButton.setStyle("-fx-background-color: #FF000022; -fx-border-color: black; -fx-border-width: 1;");

                final int r = row;
                final int c = col;
                cellButton.setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.PRIMARY)) {
                        if (_engine.getSelectedPlantType() != null) {
                            _engine.plant(_engine.getSelectedPlantType(), r, c, new Coordinate(cellButton.localToScene(cellButton.getLayoutBounds()).getCenterX(), cellButton.localToScene(cellButton.getLayoutBounds()).getCenterY()));
                            cellButton.setGraphic(null);
                            _engine.clearSelectedPlantType();
                        } else {
                            System.out.println("no plant have been selected!!!");
                        }
                    }
                });

                //
                cellButton.setOnMouseEntered(event -> {
                    var selectedType = _engine.getSelectedPlantType();
                    if (selectedType == PeashooterVisualObject.class) {
                        Platform.runLater(() -> {
                            ImageView preview = createPlantImageView("Peashooter", cellButton);
                            cellButton.setGraphic(preview);
                        });
                    } else if (selectedType == WallNutVisualObject.class) {
                        Platform.runLater(() -> {
                            ImageView preview = createPlantImageView("WallNut", cellButton);
                            cellButton.setGraphic(preview);
                        });
                    } else if (selectedType == SunFlowerVisualObject.class) {
                        Platform.runLater(() -> {
                            ImageView preview = createPlantImageView("SunFlower", cellButton);
                            cellButton.setGraphic(preview);
                        });
                    } else if (selectedType == SnowPeaVisualObject.class) {
                        Platform.runLater(() -> {
                            ImageView preview = createPlantImageView("SnowPea", cellButton);
                            cellButton.setGraphic(preview);
                        });
                    } else if (selectedType == JalapenoVisualObject.class) {
                        Platform.runLater(() -> {
                            ImageView preview = createPlantImageView("Jalapeno", cellButton);
                            cellButton.setGraphic(preview);
                        });
                    } else if (selectedType == RepeaterVisualObject.class) {
                        Platform.runLater(() -> {
                            ImageView preview = createPlantImageView("Repeater", cellButton);
                            cellButton.setGraphic(preview);
                        });
                    } else if (selectedType == TallnutVisualObject.class) {
                        Platform.runLater(() -> {
                            ImageView preview = createPlantImageView("Tallnut", cellButton);
                            cellButton.setGraphic(preview);
                        });
                    } else if (selectedType == CherryBombVisualObject.class) {
                        Platform.runLater(() -> {
                            ImageView preview = createPlantImageView("CherryBomb", cellButton);
                            cellButton.setGraphic(preview);
                        });
                    }
                });

                cellButton.setOnMouseExited(e -> {
                    FadeTransition fade = new FadeTransition(Duration.millis(150), cellButton.getGraphic());
                    fade.setFromValue(0.4);
                    fade.setToValue(0.0);
                    fade.setOnFinished(ev -> cellButton.setGraphic(null));
                    fade.play();
                });
                //
//                cellButton.setOnMouseEntered((event) -> {
//                    var demo = pick(mainContainer, event.getSceneX(), event.getSceneY());
//                    System.out.println( event.getSceneX());
//                    System.out.println( event.getSceneY());
//                    for (Node _row : mainContainer.getChildren()) {
//                        for (Node _col : ((HBox) _row).getChildren()) {
//                            if (((StackPane) _col).getChildren().get(0).equals(demo)) {
//                                System.out.println(mainContainer.getChildren().indexOf(_row) + ", " + ((HBox) _row).getChildren().indexOf(_col));
//                            }
//                        }
//                    }
//                });

                StackPane cell = new StackPane();

//                cell.setOnMouseEntered((e)->{//debug
//                    System.out.println(e.getScreenX()+" , "+e.getScreenY());
//                });
                cell.getChildren().add(cellButton);
                visualGrid[row][col] = cell;
                rowBox.getChildren().add(cell);
            }
            mainContainer.getChildren().add(rowBox);
        }
        mainContainer.layout();
        var blocks = new MapBlock[_mapObject.getRows() * _mapObject.getColumns()];
        for (int i = 0; i < _mapObject.getRows(); i++) {
            for (int j = 0; j < _mapObject.getColumns(); j++) {
                var n = ((StackPane) ((HBox) (mainContainer.getChildren().get(i))).getChildren().get(j));
                final int i_final = i;
                final int j_final = j;
                n.needsLayoutProperty().addListener((obs2, oldVal, newVal) -> { // When needsLayout is set to false, it means everything about layout bounds is set
                    if (!newVal && blocks[_mapObject.getColumns() * i_final + j_final] == null) {//need layout is false and also we havent created a mapblock
                        var x = (n.getChildren().get(0)).localToScene(n.getLayoutBounds());
                        var y = (n.getChildren().get(0)).localToScene(n.getLayoutBounds());
                        blocks[_mapObject.getColumns() * i_final + j_final] = new MapBlock(new Coordinate(x.getMinX(), y.getMinY()), new Coordinate(x.getMaxX(), y.getMaxY()), i_final, j_final);//putting that block in the blocks array with the upper left cor and the dowen right cor and also with i and j in the grid
                        _mapObject.initBlocks(blocks);
                    }
                });
            }
        }
        this._node = mainContainer;

    }

    public Button createImageButton(String imagePath) {
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);

        // تنظیم اندازه دکمه مطابق با عکس
        Button button = new Button();
        button.setGraphic(imageView);
        button.setPrefSize(image.getWidth(), image.getHeight());
        button.setMinSize(image.getWidth(), image.getHeight());
        button.setMaxSize(image.getWidth(), image.getHeight());

        // حذف padding و background پیش‌فرض دکمه
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        return button;
    }

    public ImageView createPlantImageView(String plantName, Button referenceButton) {
        String imagePath = String.format("graphics/Plants/%s/%s_0.png", plantName, plantName);
        Image image = new Image(GlobalSettings.getResource(imagePath));
        ImageView imageView = new ImageView(image);

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        double width = referenceButton.getWidth();
        double height = referenceButton.getHeight();

        imageView.setFitWidth(width - 20);
        imageView.setFitHeight(height - 10);

        imageView.setOpacity(0.4);

        return imageView;
    }



    @Override
    public void spawn() {
    }

    public void plant(AbstractVisualObject object, int row, int column) {
        object.getNode().boundsInParentProperty().addListener((observable, newVal, oldVal) -> {
            if (oldVal.getCenterX() == newVal.getCenterX()) {
                object.spawn();
            }
        });

        StackPane cell = visualGrid[row][column];
        var demo = cell.getWidth();
        ((ImageView) object.getNode()).setFitWidth(demo);
        cell.getChildren().add(object.getNode());
    }

    public void spawnByCoordinate(AbstractVisualObject object) {
        _engine.spawnVisualObject(object);
    }

    private static Node pick(Node node, double sceneX, double sceneY) {
        Point2D p = node.sceneToLocal(sceneX, sceneY, true /* rootScene */);

        // check if the given node has the point inside it, or else we drop out
        if (!node.contains(p)) return null;

        // at this point we know that _at least_ the given node is a valid
        // answer to the given point, so we will return that if we don't find
        // a better child option
        if (node instanceof Parent) {
            // we iterate through all children in reverse order, and stop when we find a match.
            // We do this as we know the elements at the end of the list have a higher
            // z-order, and are therefore the better match, compared to children that
            // might also intersect (but that would be underneath the element).
            Node bestMatchingChild = null;
            List<Node> children = ((Parent) node).getChildrenUnmodifiable();
            for (int i = children.size() - 1; i >= 0; i--) {
                Node child = children.get(i);
                p = child.sceneToLocal(sceneX, sceneY, true /* rootScene */);
                if (child.isVisible() && !child.isMouseTransparent() && child.contains(p)) {
                    bestMatchingChild = child;
                    break;
                }
            }

            if (bestMatchingChild != null) {
                return pick(bestMatchingChild, sceneX, sceneY);
            }
        }

        return node;
    }
}


